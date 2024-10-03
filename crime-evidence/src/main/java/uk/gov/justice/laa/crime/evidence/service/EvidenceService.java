package uk.gov.justice.laa.crime.evidence.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidenceItems;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.evidence.builder.EvidenceFeeRulesDTOBuilder;
import uk.gov.justice.laa.crime.evidence.common.Constants;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeRulesDTO;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiEvidenceFee;
import uk.gov.justice.laa.crime.enums.EvidenceFeeLevel;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.EvidenceFeeRules;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvidenceService {
    private final IncomeEvidenceService incomeEvidenceService;
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

    // TODO: Create a DTO once dependencies are finalised and pass in here.
    public void updateEvidence(ApiUpdateIncomeEvidenceRequest apiUpdateIncomeEvidenceRequest) {
        // Assume that we don't need to get pension details for now, instead just check that all
        // required evidence has been received.
        // Edit: going to need to get the pension details to calculate number of evidence items that
        // are required.

        List<ApiIncomeEvidence> evidenceItems = new ArrayList<>();

        // TODO: Check if we need all the null checks

        if (apiUpdateIncomeEvidenceRequest.getApplicantEvidenceItems().getIncomeEvidenceItems() != null
            && !apiUpdateIncomeEvidenceRequest.getApplicantEvidenceItems().getIncomeEvidenceItems().isEmpty()) {
            evidenceItems.addAll(apiUpdateIncomeEvidenceRequest.getApplicantEvidenceItems().getIncomeEvidenceItems());
        }

        if (apiUpdateIncomeEvidenceRequest.getPartnerEvidenceItems() != null
            && apiUpdateIncomeEvidenceRequest.getPartnerEvidenceItems().getIncomeEvidenceItems() != null
            && !apiUpdateIncomeEvidenceRequest.getPartnerEvidenceItems().getIncomeEvidenceItems().isEmpty()) {
            evidenceItems.addAll(apiUpdateIncomeEvidenceRequest.getPartnerEvidenceItems().getIncomeEvidenceItems());
        }

        // DONE: Update extra evidence - need to figure out how to obtain this.
        //  Don't need this, extra evidence is already part of the applicant and partner evidence
        //  items and is denoted by the isExtra flag. The validation logic for checking evidence
        //  items and checking extra evidence items is also identical, so there's no value in
        //  this distinction at this point.

        // TODO: Determine evidence items which need to be removed based on not being new or updated
        //  in the request.
        //  Edit: actually, just add to the in-memory list when going through the evidence and any
        //  which is not new or hasn't been updated should automatically fall off.

        if (evidenceItems.isEmpty()) {
            return;
        }

        // TODO: Get total number of evidence items required for applicant and partner
        //  Need to get the pension amounts for this, and then query the income_evidence_required
        //  table. Can't do this right now until we figure out how to get access to that table via
        //  the code which apparently has already been updated to have access to this.

        // TODO: Call the CMA service to get the financial assessment (rather than calling the
        //  Court Data API), as apparently everything should be going through the CMA.

        // TODO: Call to update assessment here (also via the CMA). Ideally keep a track of the
        //  changes that are being made as we go along directly in the financial assessment that we
        //  have retrieved, so that we can pull out the necessary values from that to use in the
        //  call to update, rather than trying to figure out what they should all be.

    }

    protected boolean isCalcRequired(CrimeEvidenceDTO crimeEvidenceDTO) {
        return (crimeEvidenceDTO.getMagCourtOutcome().equalsIgnoreCase(Constants.SENT_FOR_TRIAL)
                || crimeEvidenceDTO.getMagCourtOutcome().equalsIgnoreCase(Constants.COMMITTED_FOR_TRIAL)) &&
                (crimeEvidenceDTO.getEvidenceFee() == null || crimeEvidenceDTO.getEvidenceFee().getFeeLevel() == null);
    }

    private void isEvidenceRequired() {
        // Check if there is a linked applicant that has not been unlinked
        // If there is, get the partner employment status and applicant id




    }
}
