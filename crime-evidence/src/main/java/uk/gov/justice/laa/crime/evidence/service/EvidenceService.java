package uk.gov.justice.laa.crime.evidence.service;

import java.math.BigDecimal;
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
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRequiredRepository;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.ApplicantType;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.EvidenceFeeRules;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvidenceService {
    private final IncomeEvidenceService incomeEvidenceService;
    private final IncomeEvidenceRequiredRepository incomeEvidenceRequiredRepository;
    private final MaatCourtDataService maatCourtDataService;
    private final MeansAssessmentApiService meansAssessmentApiService;

    private static final String PrivatePensionDescription = "Income from Private Pension(s)";

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
            // TODO: This is where the stored procedure creates default income evidence - likely we
            //  should do this here so for now let's return an empty response.
            return new ApiUpdateIncomeEvidenceResponse();
        }

        ApiGetMeansAssessmentResponse oldMeansAssessmentResponse = meansAssessmentApiService.find(updateEvidenceDTO.getFinancialAssessmentId());
        Optional<ApiAssessmentDetail> assessmentDetail = getPensionAssessmentDetail(oldMeansAssessmentResponse);

        BigDecimal applicantPension = getPensionAmount(assessmentDetail, ApplicantType.APPLICANT);
        BigDecimal partnerPension = getPensionAmount(assessmentDetail, ApplicantType.PARTNER);

        boolean evidenceReceived = checkEvidenceReceived(
            applicantEvidenceItems,
            partnerEvidenceItems,
            updateEvidenceDTO.getMagCourtOutcome(),
            updateEvidenceDTO.getApplicantDetails().getEmploymentStatus(),
            updateEvidenceDTO.getPartnerDetails().getEmploymentStatus(),
            applicantPension,
            partnerPension);

        // TODO: Does this need to be set as UTC?
        LocalDateTime evidenceReceivedDate = evidenceReceived ? LocalDateTime.now() : null;

        if (evidenceReceived && oldMeansAssessmentResponse.getIncomeEvidenceSummary().getEvidenceReceivedDate() == null) {
            oldMeansAssessmentResponse.getIncomeEvidenceSummary().setEvidenceReceivedDate(evidenceReceivedDate);
        } else if (!evidenceReceived && oldMeansAssessmentResponse.getIncomeEvidenceSummary().getEvidenceReceivedDate() != null) {
            oldMeansAssessmentResponse.getIncomeEvidenceSummary().setEvidenceReceivedDate(null);
        }

        // Question: Matt said we needed this but cannot see why. The call to set_due_date in the SP
        // was checking a few edge cases (that the due date had not been removed and that the due
        // date had not been set to sometime in the past), but I think we're now already guarding
        // against this.
        LocalDateTime oldEvidenceDueDate = oldMeansAssessmentResponse.getIncomeEvidenceSummary().getEvidenceDueDate();

        // Question: should we be passing in the ApiIncomeEvidenceSummary here and setting the income
        // evidence items on that, or should we be passing in these evidence items directly to the
        // update means assessment request (and not set on the base class)?
        // OR should we be passing both and setting the evidence on both? This is not at all obvious.
        ApiUpdateMeansAssessmentRequest updateMeansAssessmentRequest = createUpdateMeansAssessmentRequest(
            oldMeansAssessmentResponse.getIncomeEvidenceSummary(),
            updateEvidenceDTO.getFinancialAssessmentId(),
            updateEvidenceDTO.getApplicantDetails().getId(),
            updateEvidenceDTO.getPartnerDetails().getId(),
            applicantEvidenceItems,
            partnerEvidenceItems
        );

        // Question: do we need this response for anything? At present it does not seem to include
        // anything that is needed for this method's return type.
        ApiMeansAssessmentResponse updateAssessmentResponse = meansAssessmentApiService.update(updateMeansAssessmentRequest);

        // TODO: Should the due date be what it was previously, or should this have been updated and
        //  available somewhere else?
        return new ApiUpdateIncomeEvidenceResponse()
            .withApplicantEvidenceItems(new ApiIncomeEvidenceItems(updateEvidenceDTO.getApplicantDetails(), applicantEvidenceItems))
            .withPartnerEvidenceItems(new ApiIncomeEvidenceItems(updateEvidenceDTO.getPartnerDetails(), partnerEvidenceItems))
            .withDueDate(oldEvidenceDueDate.toLocalDate())
            .withAllEvidenceReceivedDate(evidenceReceivedDate != null ? evidenceReceivedDate.toLocalDate() : null);
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
            .withDateReceived(apiIncomeEvidence.getDateReceived().atStartOfDay())
            .withApplicantId(applicantId)
            .withApiEvidenceType(new ApiEvidenceType(apiIncomeEvidence.getEvidenceType().getName(), apiIncomeEvidence.getEvidenceType().getDescription()));
    }

    private Optional<ApiAssessmentDetail> getPensionAssessmentDetail(ApiGetMeansAssessmentResponse meansAssessmentResponse) {
        ApiAssessmentSectionSummary assessmentSummary;

        if (meansAssessmentResponse.getFullAssessment().getAssessmentSectionSummary() != null
            && !meansAssessmentResponse.getFullAssessment().getAssessmentSectionSummary().isEmpty()) {
            assessmentSummary = meansAssessmentResponse.getFullAssessment().getAssessmentSectionSummary().stream().findFirst().get();
        }
        else {
            // NOTE: Assumption here that there is already an initial means assessment.
            assessmentSummary = meansAssessmentResponse.getInitialAssessment().getAssessmentSectionSummary().stream().findFirst().get();
        }

        return assessmentSummary.getAssessmentDetails()
            .stream()
            .filter(item -> item.getAssessmentDescription().equals(PrivatePensionDescription))
            .findFirst();
    }

    private BigDecimal getPensionAmount(Optional<ApiAssessmentDetail> assessmentDetail, ApplicantType applicantType) {
        if (assessmentDetail.isEmpty()) {
            return BigDecimal.ZERO;
        }

        ApiAssessmentDetail data = assessmentDetail.get();

        BigDecimal amount = applicantType == ApplicantType.APPLICANT ? data.getApplicantAmount() : data.getPartnerAmount();
        Frequency frequency = applicantType == ApplicantType.APPLICANT ? data.getApplicantFrequency() : data.getPartnerFrequency();

        return amount.multiply(BigDecimal.valueOf(frequency.getWeighting()));
    }

    private void updateEvidenceItemsReceivedDate(ApiIncomeEvidenceSummary incomeEvidenceSummary, LocalDateTime evidenceReceivedDate) {
        incomeEvidenceSummary.getIncomeEvidence().forEach(item -> {
            if (item.getDateReceived() == null) {
                item.setDateReceived(evidenceReceivedDate);
            }
        });
    }
}
