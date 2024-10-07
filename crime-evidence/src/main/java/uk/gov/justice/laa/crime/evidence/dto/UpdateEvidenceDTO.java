package uk.gov.justice.laa.crime.evidence.dto;

import java.util.List;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;

public class UpdateEvidenceDTO {
    private int financialAssessmentId;
    private List<ApiIncomeEvidence> applicantIncomeEvidenceItems;
    private List<ApiIncomeEvidence> partnerIncomeEvidenceItems;
    private MagCourtOutcome magCourtOutcome;
    private EmploymentStatus applicantEmploymentStatus;
    private EmploymentStatus partnerEmploymentStatus;

}
