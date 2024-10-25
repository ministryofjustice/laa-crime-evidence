package uk.gov.justice.laa.crime.evidence.builder;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateEvidenceDTOBuilder {

    public static UpdateEvidenceDTO build(final ApiUpdateIncomeEvidenceRequest request) {
        return UpdateEvidenceDTO.builder()
            .financialAssessmentId(request.getFinancialAssessmentId())
            .magCourtOutcome(request.getMagCourtOutcome())
            .applicantDetails(request.getApplicantEvidenceItems() != null ? request.getApplicantEvidenceItems().getApplicantDetails() : null)
            .applicantPensionAmount(request.getApplicantPensionAmount() != null ? request.getApplicantPensionAmount() : BigDecimal.ZERO)
            .applicationReceivedDate(request.getMetadata().getApplicationReceivedDate())
            .partnerDetails(request.getPartnerEvidenceItems() != null ? request.getPartnerEvidenceItems().getApplicantDetails() : null)
            .partnerPensionAmount(request.getPartnerPensionAmount() != null ? request.getPartnerPensionAmount() : BigDecimal.ZERO)
            .applicantIncomeEvidenceItems(request.getApplicantEvidenceItems() != null ? request.getApplicantEvidenceItems().getIncomeEvidenceItems() : null)
            .partnerIncomeEvidenceItems(request.getPartnerEvidenceItems() != null ? request.getPartnerEvidenceItems().getIncomeEvidenceItems() : null)
            .evidenceDueDate(request.getEvidenceDueDate())
            .evidenceReceivedDate(request.getEvidenceReceivedDate())
            .build();
    }
}
