package uk.gov.justice.laa.crime.evidence.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.gov.justice.laa.crime.common.model.evidence.ApiApplicantDetails;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;

@Data
@Builder
public class UpdateEvidenceDTO {
    private int financialAssessmentId;
    private List<ApiIncomeEvidence> applicantIncomeEvidenceItems;
    private List<ApiIncomeEvidence> partnerIncomeEvidenceItems;
    private MagCourtOutcome magCourtOutcome;
    private ApiApplicantDetails applicantDetails;
    private ApiApplicantDetails partnerDetails;

}
