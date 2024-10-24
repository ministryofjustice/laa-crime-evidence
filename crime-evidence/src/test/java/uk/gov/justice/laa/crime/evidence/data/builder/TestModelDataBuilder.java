package uk.gov.justice.laa.crime.evidence.data.builder;

import java.util.Collections;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.evidence.*;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.EvidenceFeeLevel;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.common.Constants;
import uk.gov.justice.laa.crime.evidence.dto.CapitalEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;
import uk.gov.justice.laa.crime.util.DateUtil;

@Component
public class TestModelDataBuilder {

    public static final Integer TEST_REP_ID = 91919;
    public static final LocalDateTime CAPITAL_EVIDENCE_RECEIVED_DATE =
            LocalDateTime.of(2023, 3, 9, 15, 1, 25);
    public static final LocalDateTime INCOME_EVIDENCE_RECEIVED_DATE =
            LocalDateTime.of(2023, 2, 19, 15, 1, 25);

    public static final String MSG_COURT_OUTCOME = "SENT FOR TRIAL";

    public static final String EMST_CODE = "SELF";
    public static final int APPLICANT_ID = 5708;
    public static final int FINANCIAL_ASSESSMENT_ID = 4509;
    public static final int PARTNER_ID = 6336;
    public static final String TEST_USER_NAME = "mock-u";
    public static final LocalDateTime DUE_DATE = LocalDateTime.of(2024, 8, 15, 0, 0, 0);
    public static final LocalDate EVIDENCE_RECEIVED_DATE = LocalDate.of(2024, 7, 12);

    public static ApiCalculateEvidenceFeeRequest getApiCalculateEvidenceFeeRequest(boolean isValid) {
        return new ApiCalculateEvidenceFeeRequest()
                .withRepId(isValid ? TEST_REP_ID : null)
                .withMagCourtOutcome(MSG_COURT_OUTCOME)
                .withEvidenceFee(getApiEvidenceFee())
                .withCapitalEvidence(getApiCapitalEvidenceList())
                .withCapitalEvidenceReceivedDate(CAPITAL_EVIDENCE_RECEIVED_DATE)
                .withIncomeEvidenceReceivedDate(INCOME_EVIDENCE_RECEIVED_DATE)
                .withEmstCode(EMST_CODE);
    }

    public static ApiCalculateEvidenceFeeResponse getApiCalculateEvidenceFeeResponse() {
        return new ApiCalculateEvidenceFeeResponse()
                .withEvidenceFee(getApiEvidenceFee());
    }

    public static ApiEvidenceFee getApiEvidenceFee() {
        return new ApiEvidenceFee()
                .withFeeLevel(EvidenceFeeLevel.LEVEL1.getFeeLevel())
                .withDescription(EvidenceFeeLevel.LEVEL1.getDescription());
    }

    public static List<ApiCapitalEvidence> getApiCapitalEvidenceList() {
        return List.of(new ApiCapitalEvidence().withEvidenceType("MOCK_TEST").withDateReceived(CAPITAL_EVIDENCE_RECEIVED_DATE),
                new ApiCapitalEvidence().withEvidenceType("MOCK_TEST1").withDateReceived(INCOME_EVIDENCE_RECEIVED_DATE));
    }

    public static ApiCalculateEvidenceFeeRequest getApiCalculateEvidenceFeeInvalidRequest() {
        return new ApiCalculateEvidenceFeeRequest()
                .withMagCourtOutcome(Constants.SENT_FOR_TRIAL)
                .withEvidenceFee(getApiEvidenceFee())
                .withCapitalEvidence(getApiCapitalEvidenceList())
                .withCapitalEvidenceReceivedDate(LocalDateTime.now())
                .withIncomeEvidenceReceivedDate(LocalDateTime.now())
                .withEmstCode("SELF");
    }

    public static ApiUserSession getUserSession() {
        return new ApiUserSession()
                .withUserName(TEST_USER_NAME)
                .withSessionId(UUID.randomUUID().toString());
    }

    public static ApiApplicantDetails getApiApplicantDetails() {
        return new ApiApplicantDetails()
                .withId(APPLICANT_ID)
                .withEmploymentStatus(EmploymentStatus.EMPLOY);
    }

    public static ApiApplicantDetails getApiPartnerDetails() {
        return getApiApplicantDetails().withId(PARTNER_ID);
    }

    public static ApiIncomeEvidenceMetadata getApiIncomeEvidenceMetadata() {
        return new ApiIncomeEvidenceMetadata()
                .withApplicationReceivedDate(LocalDate.now())
                .withNotes("mock notes")
                .withEvidencePending(false)
                .withUserSession(getUserSession());
    }

    public static ApiCreateIncomeEvidenceRequest getApiCreateIncomeEvidenceRequest() {
        return new ApiCreateIncomeEvidenceRequest()
                .withMagCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL)
                .withApplicantDetails(getApiApplicantDetails())
                .withFinancialAssessmentId(FINANCIAL_ASSESSMENT_ID)
                .withPartnerDetails(getApiPartnerDetails())
                .withMetadata(getApiIncomeEvidenceMetadata());
    }

    public static ApiIncomeEvidenceItems getApiIncomeEvidenceItems() {
        return new ApiIncomeEvidenceItems()
                .withApplicantDetails(getApiApplicantDetails())
                .withIncomeEvidenceItems(
                        List.of(
                                new ApiIncomeEvidence()
                                        .withId(9315)
                                        .withDescription("mock evidence item")
                                        .withMandatory(true)
                                        .withEvidenceType(IncomeEvidenceType.WAGE_SLIP)
                                        .withDateReceived(EVIDENCE_RECEIVED_DATE)
                                )
                );
    }

    public static ApiUpdateIncomeEvidenceRequest getApiUpdateIncomeEvidenceRequest() {
        return new ApiUpdateIncomeEvidenceRequest()
                .withEvidenceDueDate(DUE_DATE)
                .withMagCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL)
                .withFinancialAssessmentId(FINANCIAL_ASSESSMENT_ID)
                .withMetadata(getApiIncomeEvidenceMetadata());
    }

    public static CrimeEvidenceDTO getCrimeEvidenceDTO() {
        return CrimeEvidenceDTO.builder()
                .repId(TEST_REP_ID)
                .magCourtOutcome(Constants.SENT_FOR_TRIAL)
                .evidenceFee(EvidenceFeeDTO.builder().build())
                .capitalEvidence(List.of(CapitalEvidenceDTO.builder()
                        .evidenceType("")
                        .build(), CapitalEvidenceDTO.builder()
                        .evidenceType("")
                        .dateReceived(LocalDateTime.of(2023, 3, 3, 12, 12, 12))
                        .build()))
                .capitalEvidenceReceivedDate(CAPITAL_EVIDENCE_RECEIVED_DATE)
                .incomeEvidenceReceivedDate(INCOME_EVIDENCE_RECEIVED_DATE)
                .emstCode("SELF")
                .build();
    }

    public static ApiCalculateEvidenceFeeRequest getApiCalculateEvidenceFeeRequest() {
        return new ApiCalculateEvidenceFeeRequest()
                .withRepId(TEST_REP_ID)
                .withMagCourtOutcome(MSG_COURT_OUTCOME)
                .withEvidenceFee(new ApiEvidenceFee())
                .withCapitalEvidence(getApiCapitalEvidenceList())
                .withCapitalEvidenceReceivedDate(CAPITAL_EVIDENCE_RECEIVED_DATE)
                .withIncomeEvidenceReceivedDate(INCOME_EVIDENCE_RECEIVED_DATE)
                .withEmstCode(EMST_CODE);
    }

    public static UpdateEvidenceDTO getUpdateEvidenceRequest() {
        return UpdateEvidenceDTO.builder()
            .magCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL)
            .applicantIncomeEvidenceItems(Collections.emptyList())
            .partnerIncomeEvidenceItems(Collections.emptyList())
            .financialAssessmentId(FINANCIAL_ASSESSMENT_ID)
            .build();
    }

    public static UpdateEvidenceDTO getUpdateEvidenceRequest(
        LocalDate applicationReceivedDate,
        ApiApplicantDetails applicantDetails,
        List<ApiIncomeEvidence> applicantEvidenceItems,
        LocalDate evidenceDueDate,
        LocalDate evidenceReceivedDate
    ) {
        if (applicantEvidenceItems == null) {
            applicantEvidenceItems = Collections.emptyList();
        }

        return UpdateEvidenceDTO.builder()
            .magCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL)
            .applicantIncomeEvidenceItems(applicantEvidenceItems)
            .applicantDetails(applicantDetails)
            .applicationReceivedDate(applicationReceivedDate)
            .evidenceDueDate(DateUtil.convertDateToDateTime(evidenceDueDate))
            .evidenceReceivedDate(DateUtil.convertDateToDateTime(evidenceReceivedDate))
            .partnerIncomeEvidenceItems(Collections.emptyList())
            .financialAssessmentId(FINANCIAL_ASSESSMENT_ID)
            .build();
    }

    public static ApiIncomeEvidence getIncomeEvidence(IncomeEvidenceType incomeEvidenceType) {
        return new ApiIncomeEvidence()
                .withId(9315)
                .withDescription("mock evidence item")
                .withMandatory(true)
                .withEvidenceType(incomeEvidenceType)
                .withDateReceived(EVIDENCE_RECEIVED_DATE);
    }
}