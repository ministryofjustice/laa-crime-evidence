package uk.gov.justice.laa.crime.evidence.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.gov.justice.laa.crime.common.model.evidence.ApiApplicantDetails;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;

@Data
@Builder
public class UpdateEvidenceDTO {
    private List<ApiIncomeEvidence> applicantIncomeEvidenceItems;
    private List<ApiIncomeEvidence> partnerIncomeEvidenceItems;
    private MagCourtOutcome magCourtOutcome;
    private ApiApplicantDetails applicantDetails;
    private ApiApplicantDetails partnerDetails;
    private BigDecimal applicantPensionAmount;
    private BigDecimal partnerPensionAmount;
    private LocalDate applicationReceivedDate;
    private boolean evidencePending;
    private LocalDateTime evidenceDueDate;
    private LocalDateTime evidenceReceivedDate;
    private LocalDateTime previousEvidenceDueDate;
}
