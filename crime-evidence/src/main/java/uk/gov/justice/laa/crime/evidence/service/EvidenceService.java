package uk.gov.justice.laa.crime.evidence.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidenceItems;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiEvidenceType;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidenceSummary;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.Frequency;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.evidence.builder.EvidenceFeeRulesDTOBuilder;
import uk.gov.justice.laa.crime.evidence.common.Constants;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeRulesDTO;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiEvidenceFee;
import uk.gov.justice.laa.crime.enums.EvidenceFeeLevel;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceReceivedResultDTO;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.ApplicantType;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.EvidenceFeeRules;
import uk.gov.justice.laa.crime.util.DateUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvidenceService {
    private final IncomeEvidenceService incomeEvidenceService;
    private final IncomeEvidenceValidationService incomeEvidenceValidationService;
    private final MaatCourtDataService maatCourtDataService;
    private final MeansAssessmentApiService meansAssessmentApiService;

    public ApiCalculateEvidenceFeeResponse calculateEvidenceFee(CrimeEvidenceDTO crimeEvidenceDTO) {
        ApiCalculateEvidenceFeeResponse apiProcessRepOrderResponse = new ApiCalculateEvidenceFeeResponse();
        String incomeEvidenceReceived = "N";
        String capitalEvidenceReceived = "N";
        Long capEvidenceCount = null;
        long capEvidenceOutstandingCount = 0;

        if (isCalcRequired(crimeEvidenceDTO)) {

            if (crimeEvidenceDTO.getCapitalEvidence() != null) {
                capEvidenceCount = crimeEvidenceDTO.getCapitalEvidence().stream().filter(f -> f.getDateReceived() != null).count();
            }

            if (capEvidenceCount != null) {
                capEvidenceOutstandingCount = crimeEvidenceDTO.getCapitalEvidence().stream().filter(f -> f.getDateReceived() == null).count();
                capEvidenceCount = maatCourtDataService.getRepOrderCapitalByRepId(crimeEvidenceDTO.getRepId());
            }

            if (null != crimeEvidenceDTO.getIncomeEvidenceReceivedDate()) {
                incomeEvidenceReceived = "Y";
            }

            if (null != crimeEvidenceDTO.getCapitalEvidenceReceivedDate() || capEvidenceCount == null || capEvidenceOutstandingCount == 0) {
                capitalEvidenceReceived = "Y";
            }

            EvidenceFeeRulesDTO evidenceFeeRulesDTO = EvidenceFeeRulesDTOBuilder.build(
                    crimeEvidenceDTO.getEmstCode(),
                    capitalEvidenceReceived,
                    incomeEvidenceReceived,
                    capEvidenceCount,
                    capEvidenceCount
            );
            EvidenceFeeRules evidenceFeeRules = EvidenceFeeRules.getFrom(evidenceFeeRulesDTO);
            if (evidenceFeeRules != null) {
                EvidenceFeeLevel evidenceFeeLevel = EvidenceFeeLevel.getFrom(evidenceFeeRules.getEvidenceFeeLevel().toString());
                if (evidenceFeeLevel != null) {
                    apiProcessRepOrderResponse.withEvidenceFee(new ApiEvidenceFee()
                            .withFeeLevel(evidenceFeeLevel.getFeeLevel())
                            .withDescription(evidenceFeeLevel.getDescription()));
                }
            }
        }
        return apiProcessRepOrderResponse;
    }

    public ApiUpdateIncomeEvidenceResponse updateEvidence(UpdateEvidenceDTO updateEvidenceDTO) {
        List<ApiIncomeEvidence> applicantEvidenceItems = updateEvidenceDTO.getApplicantIncomeEvidenceItems();
        List<ApiIncomeEvidence> partnerEvidenceItems = updateEvidenceDTO.getPartnerIncomeEvidenceItems();

        if (applicantEvidenceItems.isEmpty() && partnerEvidenceItems.isEmpty()) {
            throw new IllegalArgumentException("No income evidence items provided");
        }

        ApiGetMeansAssessmentResponse oldMeansAssessmentResponse = meansAssessmentApiService.find(updateEvidenceDTO.getFinancialAssessmentId());
        ApiIncomeEvidenceSummary incomeEvidenceSummary = oldMeansAssessmentResponse.getIncomeEvidenceSummary();

        incomeEvidenceValidationService.checkEvidenceDueDates(
            DateUtil.toDate(updateEvidenceDTO.getEvidenceDueDate()),
            DateUtil.toDate(incomeEvidenceSummary.getFirstReminderDate()),
            DateUtil.toDate(incomeEvidenceSummary.getSecondReminderDate()),
            DateUtil.toDate(incomeEvidenceSummary.getEvidenceDueDate()));

        incomeEvidenceValidationService.checkEvidenceReceivedDate(
            DateUtil.toDate(updateEvidenceDTO.getEvidenceReceivedDate()),
            DateUtil.asDate(updateEvidenceDTO.getApplicationReceivedDate()));

        boolean evidenceReceived = checkEvidenceReceived(
            applicantEvidenceItems,
            partnerEvidenceItems,
            updateEvidenceDTO.getMagCourtOutcome(),
            updateEvidenceDTO.getApplicantDetails().getEmploymentStatus(),
            updateEvidenceDTO.getPartnerDetails().getEmploymentStatus(),
            updateEvidenceDTO.getApplicantPensionAmount(),
            updateEvidenceDTO.getPartnerPensionAmount());

        updateEvidenceReceivedDate(incomeEvidenceSummary, evidenceReceived, updateEvidenceDTO.getEvidenceReceivedDate());
        updateEvidenceDueDate(incomeEvidenceSummary, updateEvidenceDTO.getEvidenceDueDate());

        ApiUpdateMeansAssessmentRequest updateMeansAssessmentRequest = createUpdateMeansAssessmentRequest(
            incomeEvidenceSummary,
            updateEvidenceDTO.getFinancialAssessmentId(),
            updateEvidenceDTO.getApplicantDetails().getId(),
            updateEvidenceDTO.getPartnerDetails().getId(),
            applicantEvidenceItems,
            partnerEvidenceItems
        );

        meansAssessmentApiService.update(updateMeansAssessmentRequest);

        // TODO: Map ids of new income evidence items.

        // TODO: Should the due date be what it was previously, or should this have been updated and
        //  available somewhere else?
        return new ApiUpdateIncomeEvidenceResponse()
            .withApplicantEvidenceItems(new ApiIncomeEvidenceItems(updateEvidenceDTO.getApplicantDetails(), applicantEvidenceItems))
            .withPartnerEvidenceItems(new ApiIncomeEvidenceItems(updateEvidenceDTO.getPartnerDetails(), partnerEvidenceItems))
            .withDueDate(DateUtil.parseLocalDate(incomeEvidenceSummary.getEvidenceDueDate()))
            .withAllEvidenceReceivedDate(DateUtil.parseLocalDate(incomeEvidenceSummary.getEvidenceReceivedDate()));
    }

    protected boolean isCalcRequired(CrimeEvidenceDTO crimeEvidenceDTO) {
        return (crimeEvidenceDTO.getMagCourtOutcome().equalsIgnoreCase(Constants.SENT_FOR_TRIAL)
                || crimeEvidenceDTO.getMagCourtOutcome().equalsIgnoreCase(Constants.COMMITTED_FOR_TRIAL)) &&
                (crimeEvidenceDTO.getEvidenceFee() == null || crimeEvidenceDTO.getEvidenceFee().getFeeLevel() == null);
    }

    private boolean checkEvidenceReceived(
        List<ApiIncomeEvidence> applicantEvidenceItems,
        List<ApiIncomeEvidence> partnerEvidenceItems,
        MagCourtOutcome magCourtOutcome,
        EmploymentStatus applicantEmploymentStatus,
        EmploymentStatus partnerEmploymentStatus,
        BigDecimal applicantPensionAmount,
        BigDecimal partnerPensionAmount) {
        EvidenceReceivedResultDTO applicantEvidenceReceivedResult = incomeEvidenceService.checkMinimumEvidenceItemsReceived(
            applicantEvidenceItems,
            ApplicantType.APPLICANT,
            magCourtOutcome,
            applicantEmploymentStatus,
            partnerEmploymentStatus,
            applicantPensionAmount
        );

        if (!applicantEvidenceReceivedResult.isEvidenceReceived()) {
            return false;
        }

        if (!partnerEvidenceItems.isEmpty()) {
            EvidenceReceivedResultDTO partnerEvidenceReceivedResult = incomeEvidenceService.checkMinimumEvidenceItemsReceived(
                partnerEvidenceItems,
                ApplicantType.PARTNER,
                magCourtOutcome,
                applicantEmploymentStatus,
                partnerEmploymentStatus,
                partnerPensionAmount
            );

            if (!partnerEvidenceReceivedResult.isEvidenceReceived()) {
                return false;
            }

            boolean partnerRequiredEvidenceOutstanding = incomeEvidenceService.isRequiredEvidenceOutstanding(
                partnerEvidenceReceivedResult.getMinimumEvidenceItemsRequired(), partnerEvidenceItems);

            if (partnerRequiredEvidenceOutstanding) {
                return false;
            }
        }

        boolean applicantRequiredEvidenceOutstanding = incomeEvidenceService.isRequiredEvidenceOutstanding(
            applicantEvidenceReceivedResult.getMinimumEvidenceItemsRequired(), applicantEvidenceItems);

        return !applicantRequiredEvidenceOutstanding;
    }

    private ApiUpdateMeansAssessmentRequest createUpdateMeansAssessmentRequest(
        ApiIncomeEvidenceSummary currentIncomeEvidenceSummary,
        int financialAssessmentId,
        int applicantId,
        int partnerId,
        List<ApiIncomeEvidence> applicantEvidenceItems,
        List<ApiIncomeEvidence> partnerEvidenceItems) {
        // Note: the ApiIncomeEvidenceSummary is being passed only to update the evidence due and
        // received dates. Evidence items are passed directly as part of the request and not set via
        // the evidence summary object.
        List<uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidence> incomeEvidenceItems = new ArrayList<>();
        applicantEvidenceItems.forEach(applicantEvidenceItem -> incomeEvidenceItems.add(mapApiIncomeEvidence(applicantEvidenceItem, applicantId)));
        partnerEvidenceItems.forEach(partnerEvidenceItem -> incomeEvidenceItems.add(mapApiIncomeEvidence(partnerEvidenceItem, partnerId)));

        return new ApiUpdateMeansAssessmentRequest()
            .withFinancialAssessmentId(financialAssessmentId)
            .withIncomeEvidence(incomeEvidenceItems)
            .withIncomeEvidenceSummary(currentIncomeEvidenceSummary);
    }

    private uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidence mapApiIncomeEvidence(ApiIncomeEvidence apiIncomeEvidence, int applicantId) {
        return new uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidence()
            .withId(apiIncomeEvidence.getId())
            .withDateReceived(DateUtil.convertDateToDateTime(apiIncomeEvidence.getDateReceived()))
            .withApplicantId(applicantId)
            .withApiEvidenceType(new ApiEvidenceType(apiIncomeEvidence.getEvidenceType().getName(), apiIncomeEvidence.getEvidenceType().getDescription()));
    }

    private void updateEvidenceDueDate(ApiIncomeEvidenceSummary incomeEvidenceSummary, LocalDateTime evidenceDueDate) {
        LocalDateTime previousEvidenceDueDate = incomeEvidenceSummary.getEvidenceDueDate();

        if (evidenceDueDate == null && previousEvidenceDueDate != null) {
            incomeEvidenceSummary.setEvidenceDueDate(previousEvidenceDueDate);
        } else if (LocalDateTime.now().isAfter(evidenceDueDate) && evidenceDueDate != previousEvidenceDueDate) {
            incomeEvidenceSummary.setEvidenceDueDate(previousEvidenceDueDate);
        }

        // TODO: What should the due date be set as if the provided evidenceDueDate is in the past
        //  and the previousEvidenceDueDate is null? SP just raises an exception.


    }

    private void updateEvidenceReceivedDate(ApiIncomeEvidenceSummary incomeEvidenceSummary, boolean evidenceReceived, LocalDateTime evidenceReceivedDate) {
        if (evidenceReceivedDate == null) {
            evidenceReceivedDate = evidenceReceived ? LocalDateTime.now() : null;
        }

        if (evidenceReceived && incomeEvidenceSummary.getEvidenceReceivedDate() == null) {
            incomeEvidenceSummary.setEvidenceReceivedDate(evidenceReceivedDate);
        } else if (!evidenceReceived && incomeEvidenceSummary.getEvidenceReceivedDate() != null) {
            incomeEvidenceSummary.setEvidenceReceivedDate(null);
        }
    }
}
