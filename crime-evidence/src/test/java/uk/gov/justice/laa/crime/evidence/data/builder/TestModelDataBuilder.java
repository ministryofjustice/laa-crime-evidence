package uk.gov.justice.laa.crime.evidence.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.model.common.ApiCalculateEvidenceFeeRequest;
import uk.gov.justice.laa.crime.evidence.model.common.ApiCalculateEvidenceFeeResponse;
import uk.gov.justice.laa.crime.evidence.model.common.ApiCapitalEvidence;
import uk.gov.justice.laa.crime.evidence.model.common.ApiEvidenceFee;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.EvidenceFeeLevel;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TestModelDataBuilder {

    public static final Integer TEST_REP_ID = 91919;
    public static final String MEANS_ASSESSMENT_TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";

    public static final LocalDateTime CAPITAL_EVIDENCE_RECEIVED_DATE =
            LocalDateTime.of(2023, 03, 9, 15, 1, 25);
    public static final LocalDateTime  INCOME_EVIDENCE_RECEIVED_DATE =
            LocalDateTime.of(2023, 02, 19, 15, 1, 25);

    public static final String MSG_COURT_OUTCOME = "SENT FOR TRIAL";

    public static final String EMST_CODE = "SELF";


    public static ApiCalculateEvidenceFeeRequest getApiCalculateEvidenceFeeRequest(boolean isValid) {
        return new ApiCalculateEvidenceFeeRequest()
                .withRepId(isValid ? TEST_REP_ID : null)
                .withLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
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
                .withLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .withMagCourtOutcome("SENT FOR TRIAL")
                .withEvidenceFee(getApiEvidenceFee())
                .withCapitalEvidence(getApiCapitalEvidenceList())
                .withCapitalEvidenceReceivedDate(LocalDateTime.now())
                .withIncomeEvidenceReceivedDate(LocalDateTime.now())
                .withEmstCode("SELF");
    }

    public static CrimeEvidenceDTO getCalculateEvidenceFeeDTO() {
        return CrimeEvidenceDTO.builder()
                .repId(TEST_REP_ID)
                .laaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .magCourtOutcome("SENT FOR TRIAL")
                .build();
    }


}