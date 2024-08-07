package uk.gov.justice.laa.crime.evidence.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.evidence.common.Constants;
import uk.gov.justice.laa.crime.evidence.dto.CapitalEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeDTO;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCapitalEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiEvidenceFee;
import uk.gov.justice.laa.crime.enums.EvidenceFeeLevel;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TestModelDataBuilder {

    public static final Integer TEST_REP_ID = 91919;
    public static final LocalDateTime CAPITAL_EVIDENCE_RECEIVED_DATE =
            LocalDateTime.of(2023, 3, 9, 15, 1, 25);
    public static final LocalDateTime INCOME_EVIDENCE_RECEIVED_DATE =
            LocalDateTime.of(2023, 2, 19, 15, 1, 25);

    public static final String MSG_COURT_OUTCOME = "SENT FOR TRIAL";

    public static final String EMST_CODE = "SELF";

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
}