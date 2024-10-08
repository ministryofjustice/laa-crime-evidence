package uk.gov.justice.laa.crime.evidence.builder;

import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;

public class UpdateEvidenceDTOBuilder {

    public static UpdateEvidenceDTO build(final ApiUpdateIncomeEvidenceRequest request) {
        return UpdateEvidenceDTO.builder()
            .financialAssessmentId(request.getFinancialAssessmentId())
            .magCourtOutcome(request.getMagCourtOutcome())
            .applicantEmploymentStatus(request.getApplicantEvidenceItems().getApplicantDetails().getEmploymentStatus())
            .partnerEmploymentStatus(request.getPartnerEvidenceItems().getApplicantDetails().getEmploymentStatus())
            .applicantIncomeEvidenceItems(request.getApplicantEvidenceItems().getIncomeEvidenceItems())
            .partnerIncomeEvidenceItems(request.getPartnerEvidenceItems().getIncomeEvidenceItems())
            .build();
    }
}
