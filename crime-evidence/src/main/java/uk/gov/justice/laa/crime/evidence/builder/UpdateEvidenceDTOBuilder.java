package uk.gov.justice.laa.crime.evidence.builder;

import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;

public class UpdateEvidenceDTOBuilder {

    public static UpdateEvidenceDTO build(final ApiUpdateIncomeEvidenceRequest request) {
        return UpdateEvidenceDTO.builder()
            .financialAssessmentId(request.getFinancialAssessmentId())
            .magCourtOutcome(request.getMagCourtOutcome())
            .applicantDetails(request.getApplicantEvidenceItems().getApplicantDetails())
            .partnerDetails(request.getPartnerEvidenceItems().getApplicantDetails())
            .applicantIncomeEvidenceItems(request.getApplicantEvidenceItems().getIncomeEvidenceItems())
            .partnerIncomeEvidenceItems(request.getPartnerEvidenceItems().getIncomeEvidenceItems())
            .build();
    }
}
