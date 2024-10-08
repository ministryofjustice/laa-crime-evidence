package uk.gov.justice.laa.crime.evidence.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.evidence.builder.EvidenceFeeRulesDTOBuilder;
import uk.gov.justice.laa.crime.evidence.common.Constants;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeRulesDTO;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiEvidenceFee;
import uk.gov.justice.laa.crime.enums.EvidenceFeeLevel;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRequiredRepository;
import uk.gov.justice.laa.crime.evidence.staticdata.entity.IncomeEvidenceRequiredEntity;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.EvidenceFeeRules;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvidenceService {
    private final IncomeEvidenceService incomeEvidenceService;
    private final IncomeEvidenceRequiredRepository incomeEvidenceRequiredRepository;
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
            // TODO: This is where the stored procedure creates default income evidence - likely we
            //  should do this here so for now let's return an empty response.
            return new ApiUpdateIncomeEvidenceResponse();
        }

        // TODO: Call the CMA service to get the financial assessment (rather than calling the
        //  Court Data API), as apparently everything should be going through the CMA.
        // TODO: Map this response into some appropriate DTO later on, for now use whatever we need
        //  directly from it.
        ApiGetMeansAssessmentResponse meansAssessmentResponse = meansAssessmentApiService.find(updateEvidenceDTO.getFinancialAssessmentId());
        ApiAssessmentDetail assessmentDetail = getAssessmentDetail(meansAssessmentResponse);

        BigDecimal applicantPension = getApplicantPensionAmount(assessmentDetail);
        BigDecimal partnerPension = getPartnerPensionAmount(assessmentDetail);

        boolean evidenceReceived = checkEvidenceReceived(
            applicantEvidenceItems,
            partnerEvidenceItems,
            updateEvidenceDTO.getMagCourtOutcome(),
            updateEvidenceDTO.getApplicantEmploymentStatus(),
            updateEvidenceDTO.getPartnerEmploymentStatus(),
            applicantPension,
            partnerPension);

        // TODO: Call to update assessment here (also via the CMA). Ideally keep a track of the
        //  changes that are being made as we go along directly in the financial assessment that we
        //  have retrieved, so that we can pull out the necessary values from that to use in the
        //  call to update, rather than trying to figure out what they should all be.
        // TODO: Need to update only the evidence received and due dates, as well as passing the
        //  income evidence objects here.
        LocalDateTime oldEvidenceDueDate = meansAssessmentResponse.getIncomeEvidenceSummary().getEvidenceDueDate();
        // TODO: Possibly make sure this is set to UTC.
        LocalDateTime evidenceReceivedDate = evidenceReceived ? LocalDateTime.now() : null;

        // TODO: Update relevant properties on the means assessment response received earlier. Will
        //  also want to update the evidence received date on all evidence items where it is not set
        //  if all evidence is determined to have been receieved (assumption).
        meansAssessmentResponse.getIncomeEvidenceSummary().setEvidenceReceivedDate(evidenceReceivedDate);

        ApiUpdateMeansAssessmentRequest updateMeansAssessmentRequest = new ApiUpdateMeansAssessmentRequest()
            .withFinancialAssessmentId(BigDecimal.valueOf(updateEvidenceDTO.getFinancialAssessmentId()))
            .withIncomeEvidenceSummary(meansAssessmentResponse.getIncomeEvidenceSummary());


        ApiMeansAssessmentResponse updateAssessmentResponse = meansAssessmentApiService.update(new ApiUpdateMeansAssessmentRequest());

        // TODO: The response should include the API income evidence so that we can construct an
        //  ApiUpdateIncomeEvidenceResponse payload, which is what the controller is currently set
        //  to return.
        return new ApiUpdateIncomeEvidenceResponse();
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
        List<IncomeEvidenceRequiredEntity> applicantRequiredEvidenceItems = incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(
            magCourtOutcome.getOutcome(),
            applicantEmploymentStatus.getCode(),
            partnerEmploymentStatus.getCode(),
            "APPLICANT",
            applicantPensionAmount.doubleValue());

        if (!partnerEvidenceItems.isEmpty()) {
            List<IncomeEvidenceRequiredEntity> partnerRequiredEvidenceItems = incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(
                magCourtOutcome.getOutcome(),
                applicantEmploymentStatus.getCode(),
                partnerEmploymentStatus.getCode(),
                "PARTNER",
                partnerPensionAmount.doubleValue());

            if (!checkRequiredEvidenceItemsReceived(partnerRequiredEvidenceItems, partnerEvidenceItems)) {
                return false;
            }
        }

        return checkRequiredEvidenceItemsReceived(applicantRequiredEvidenceItems, applicantEvidenceItems);
    }

    private boolean checkRequiredEvidenceItemsReceived(List<IncomeEvidenceRequiredEntity> requiredEvidenceItems, List<ApiIncomeEvidence> providedEvidenceItems) {
        return providedEvidenceItems.size() >= requiredEvidenceItems.size();
    }

    private ApiAssessmentDetail getAssessmentDetail(ApiGetMeansAssessmentResponse meansAssessmentResponse) {
        if (meansAssessmentResponse.getFullAssessment().getAssessmentSectionSummary() != null
            && !meansAssessmentResponse.getFullAssessment().getAssessmentSectionSummary().isEmpty()) {

            // TODO: Which assessment summary should we be getting here, the last one presumably (if ordered)?
            ApiAssessmentSectionSummary assessmentSummary = meansAssessmentResponse.getFullAssessment().getAssessmentSectionSummary().stream().findFirst().get();

            // TODO: Which assessment detail should we be getting?
            ApiAssessmentDetail assessmentDetail = assessmentSummary.getAssessmentDetails().stream().findFirst().get();

            return assessmentDetail;
        }

        // TODO: Can we assume here that an initial means assessment must already have been completed
        //  (given that the initial request from MAAT was to update evidence)?

        ApiAssessmentSectionSummary assessmentSummary = meansAssessmentResponse.getInitialAssessment().getAssessmentSectionSummary().stream().findFirst().get();

        ApiAssessmentDetail assessmentDetail = assessmentSummary.getAssessmentDetails().stream().findFirst().get();

        return assessmentDetail;
    }

    private BigDecimal getApplicantPensionAmount(ApiAssessmentDetail assessmentDetail) {
        // TODO: Is this correct?
        return assessmentDetail.getApplicantAmount().multiply(BigDecimal.valueOf(assessmentDetail.getApplicantFrequency().getWeighting()));
    }

    private BigDecimal getPartnerPensionAmount(ApiAssessmentDetail assessmentDetail) {
        // TODO: Is this correct?
        return assessmentDetail.getPartnerAmount().multiply(BigDecimal.valueOf(assessmentDetail.getPartnerFrequency().getWeighting()));
    }

}
