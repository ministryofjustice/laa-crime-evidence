package uk.gov.justice.laa.crime.evidence.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.justice.laa.crime.common.model.evidence.ApiApplicantDetails;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;

import java.math.BigDecimal;

@Data
@Builder
public class CreateEvidenceDTO {
    private MagCourtOutcome magCourtOutcome;
    private ApiApplicantDetails applicantDetails;
    private ApiApplicantDetails partnerDetails;
    private BigDecimal applicantPensionAmount;
    private BigDecimal partnerPensionAmount;
}
