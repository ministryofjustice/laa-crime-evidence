package uk.gov.justice.laa.crime.evidence.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidenceItems;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiEvidenceType;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidenceSummary;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
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

        incomeEvidenceValidationService.checkEvidenceReceivedDate(
            DateUtil.parseLocalDate(updateEvidenceDTO.getEvidenceReceivedDate()),
            updateEvidenceDTO.getApplicationReceivedDate());

        ApiGetMeansAssessmentResponse oldMeansAssessmentResponse = meansAssessmentApiService.find(updateEvidenceDTO.getFinancialAssessmentId());
        ApiIncomeEvidenceSummary incomeEvidenceSummary = oldMeansAssessmentResponse.getIncomeEvidenceSummary();

        incomeEvidenceValidationService.checkEvidenceDueDates(
            DateUtil.parseLocalDate(updateEvidenceDTO.getEvidenceDueDate()),
            DateUtil.parseLocalDate(incomeEvidenceSummary.getFirstReminderDate()),
            DateUtil.parseLocalDate(incomeEvidenceSummary.getSecondReminderDate()),
            DateUtil.parseLocalDate(incomeEvidenceSummary.getEvidenceDueDate()));

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

        ApiMeansAssessmentResponse updateAssessmentResponse = updateMeansAssessment(
            incomeEvidenceSummary,
            updateEvidenceDTO.getFinancialAssessmentId(),
            updateEvidenceDTO.getApplicantDetails().getId(),
            updateEvidenceDTO.getPartnerDetails().getId(),
            applicantEvidenceItems,
            partnerEvidenceItems
        );

        applicantEvidenceItems = getUpdatedEvidenceItems(updateAssessmentResponse, updateEvidenceDTO.getApplicantDetails().getId());
        partnerEvidenceItems = getUpdatedEvidenceItems(updateAssessmentResponse, updateEvidenceDTO.getPartnerDetails().getId());
        
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
                partnerEvidenceReceivedResult.getIncomeEvidenceRequiredId(), partnerEvidenceItems);

            if (partnerRequiredEvidenceOutstanding) {
                return false;
            }
        }

        boolean applicantRequiredEvidenceOutstanding = incomeEvidenceService.isRequiredEvidenceOutstanding(
            applicantEvidenceReceivedResult.getIncomeEvidenceRequiredId(), applicantEvidenceItems);

        return !applicantRequiredEvidenceOutstanding;
    }

    private List<ApiIncomeEvidence> getUpdatedEvidenceItems(
        ApiMeansAssessmentResponse meansAssessmentResponse,
        int personId) {
        return meansAssessmentResponse.getIncomeEvidence()
            .stream()
            .filter(evidence -> evidence.getApplicantId() == personId)
            .map(this::mapApiIncomeEvidence)
            .toList();
    }

    private ApiMeansAssessmentResponse updateMeansAssessment(
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

        ApiUpdateMeansAssessmentRequest request = new ApiUpdateMeansAssessmentRequest()
            .withFinancialAssessmentId(financialAssessmentId)
            .withIncomeEvidence(incomeEvidenceItems)
            .withIncomeEvidenceSummary(currentIncomeEvidenceSummary);

        return meansAssessmentApiService.update(request);
    }

    private uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidence mapApiIncomeEvidence(ApiIncomeEvidence apiIncomeEvidence, int applicantId) {
        return new uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidence()
            .withId(apiIncomeEvidence.getId())
            .withApplicantId(applicantId)
            .withDateReceived(DateUtil.convertDateToDateTime(apiIncomeEvidence.getDateReceived()))
            .withApiEvidenceType(new ApiEvidenceType(apiIncomeEvidence.getEvidenceType().getName(), apiIncomeEvidence.getEvidenceType().getDescription()));
    }

    private ApiIncomeEvidence mapApiIncomeEvidence(
        uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidence apiIncomeEvidence) {
        return new ApiIncomeEvidence()
            .withId(apiIncomeEvidence.getId())
            .withDateReceived(DateUtil.parseLocalDate(apiIncomeEvidence.getDateReceived()))
            .withDescription(apiIncomeEvidence.getOtherText())
            .withEvidenceType(IncomeEvidenceType.getFrom(apiIncomeEvidence.getApiEvidenceType().getCode()));
    }

    private void updateEvidenceDueDate(ApiIncomeEvidenceSummary incomeEvidenceSummary, LocalDateTime evidenceDueDate) {
        LocalDateTime previousEvidenceDueDate = incomeEvidenceSummary.getEvidenceDueDate();

        if (evidenceDueDate == null && previousEvidenceDueDate != null) {
            incomeEvidenceSummary.setEvidenceDueDate(previousEvidenceDueDate);
        } else if (LocalDateTime.now().isAfter(evidenceDueDate) && evidenceDueDate != previousEvidenceDueDate) {
            incomeEvidenceSummary.setEvidenceDueDate(previousEvidenceDueDate);
        }
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
