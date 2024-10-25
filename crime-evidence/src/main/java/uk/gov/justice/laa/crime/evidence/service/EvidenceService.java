package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiEvidenceFee;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidenceItems;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.enums.EvidenceFeeLevel;
import uk.gov.justice.laa.crime.evidence.builder.EvidenceFeeRulesDTOBuilder;
import uk.gov.justice.laa.crime.evidence.common.Constants;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeRulesDTO;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.ApplicantType;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.EvidenceFeeRules;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvidenceService {
    private final IncomeEvidenceService incomeEvidenceService;
    private final IncomeEvidenceValidationService incomeEvidenceValidationService;
    private final MaatCourtDataService maatCourtDataService;

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

        incomeEvidenceValidationService.checkExtraEvidenceDescriptions(applicantEvidenceItems);
        incomeEvidenceValidationService.checkExtraEvidenceDescriptions(partnerEvidenceItems);

        incomeEvidenceValidationService.checkEvidenceDueDates(
            DateUtil.parseLocalDate(updateEvidenceDTO.getEvidenceDueDate()),
            DateUtil.parseLocalDate(updateEvidenceDTO.getPreviousEvidenceDueDate()),
            updateEvidenceDTO.isEvidencePending());

        boolean applicantEvidenceItemsReceived = true;
        boolean partnerEvidenceItemsReceived = true;

        if (!applicantEvidenceItems.isEmpty()) {
            applicantEvidenceItemsReceived = incomeEvidenceService.checkEvidenceReceived(
                applicantEvidenceItems,
                updateEvidenceDTO.getMagCourtOutcome(),
                updateEvidenceDTO.getApplicantDetails().getEmploymentStatus(),
                updateEvidenceDTO.getPartnerDetails() != null
                    ? updateEvidenceDTO.getPartnerDetails().getEmploymentStatus() : null,
                updateEvidenceDTO.getApplicantPensionAmount(),
                ApplicantType.APPLICANT);
        }

        if (!partnerEvidenceItems.isEmpty()) {
            partnerEvidenceItemsReceived = incomeEvidenceService.checkEvidenceReceived(
                partnerEvidenceItems,
                updateEvidenceDTO.getMagCourtOutcome(),
                updateEvidenceDTO.getApplicantDetails().getEmploymentStatus(),
                updateEvidenceDTO.getPartnerDetails().getEmploymentStatus(),
                updateEvidenceDTO.getPartnerPensionAmount(),
                ApplicantType.PARTNER);
        }

        boolean allEvidenceItemsReceived = applicantEvidenceItemsReceived && partnerEvidenceItemsReceived;

        updateEvidenceReceivedDate(updateEvidenceDTO, allEvidenceItemsReceived);
        updateEvidenceDueDate(updateEvidenceDTO);

        ApiUpdateIncomeEvidenceResponse response = new ApiUpdateIncomeEvidenceResponse()
            .withApplicantEvidenceItems(new ApiIncomeEvidenceItems(updateEvidenceDTO.getApplicantDetails(), applicantEvidenceItems))
            .withDueDate(DateUtil.parseLocalDate(updateEvidenceDTO.getEvidenceDueDate()))
            .withAllEvidenceReceivedDate(DateUtil.parseLocalDate(updateEvidenceDTO.getEvidenceReceivedDate()));

        if (!partnerEvidenceItems.isEmpty()) {
            response.setPartnerEvidenceItems(new ApiIncomeEvidenceItems(updateEvidenceDTO.getPartnerDetails(), partnerEvidenceItems));
        }

        return response;
    }

    protected boolean isCalcRequired(CrimeEvidenceDTO crimeEvidenceDTO) {
        return (crimeEvidenceDTO.getMagCourtOutcome().equalsIgnoreCase(Constants.SENT_FOR_TRIAL)
                || crimeEvidenceDTO.getMagCourtOutcome().equalsIgnoreCase(Constants.COMMITTED_FOR_TRIAL)) &&
                (crimeEvidenceDTO.getEvidenceFee() == null || crimeEvidenceDTO.getEvidenceFee().getFeeLevel() == null);
    }

    private void updateEvidenceDueDate(UpdateEvidenceDTO updateEvidenceDTO) {
        LocalDateTime previousEvidenceDueDate = updateEvidenceDTO.getPreviousEvidenceDueDate();

        if (updateEvidenceDTO.getEvidenceDueDate() == null && previousEvidenceDueDate != null) {
            updateEvidenceDTO.setEvidenceDueDate(previousEvidenceDueDate);
        }
    }

    private void updateEvidenceReceivedDate(UpdateEvidenceDTO updateEvidenceDTO, boolean evidenceReceived) {
        if (evidenceReceived && updateEvidenceDTO.getEvidenceReceivedDate() == null) {
            updateEvidenceDTO.setEvidenceReceivedDate(LocalDateTime.now());
        } else if (!evidenceReceived && updateEvidenceDTO.getEvidenceReceivedDate() != null) {
            updateEvidenceDTO.setEvidenceReceivedDate(null);
        }
    }
}
