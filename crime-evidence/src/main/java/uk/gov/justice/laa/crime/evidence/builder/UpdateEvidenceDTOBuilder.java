package uk.gov.justice.laa.crime.evidence.builder;

import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;

public class UpdateEvidenceDTOBuilder {

    public static UpdateEvidenceDTO build(final ApiUpdateIncomeEvidenceRequest request) {
        return UpdateEvidenceDTO.builder()
            .financialAssessmentId(request.getFinancialAssessmentId())
            .magCourtOutcome(request.getMagCourtOutcome())
            .applicantDetails(request.getApplicantEvidenceItems().getApplicantDetails())
            .applicantPensionAmount(request.getApplicantPensionAmount())
            .applicationReceivedDate(request.getMetadata().getApplicationReceivedDate())
            .partnerDetails(request.getPartnerEvidenceItems().getApplicantDetails())
            .partnerPensionAmount(request.getPartnerPensionAmount())
            .applicantIncomeEvidenceItems(request.getApplicantEvidenceItems().getIncomeEvidenceItems())
            .partnerIncomeEvidenceItems(request.getPartnerEvidenceItems().getIncomeEvidenceItems())
            .evidenceDueDate(request.getEvidenceDueDate())
            .evidenceReceivedDate(request.getEvidenceReceivedDate())
            .build();
    }
}
