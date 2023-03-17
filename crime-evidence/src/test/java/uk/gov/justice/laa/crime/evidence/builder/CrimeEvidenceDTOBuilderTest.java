package uk.gov.justice.laa.crime.evidence.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.dto.CapitalEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.EvidenceFeeLevel;

import java.util.List;

@ExtendWith(SoftAssertionsExtension.class)
class CrimeEvidenceDTOBuilderTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiCalculateEvidenceFeeRequest_whenBuildIsInvoked_thenCorrectCrimeEvidenceDTOFieldsArePopulated() {
        CrimeEvidenceDTO crimeEvidenceDTO = CrimeEvidenceDTOBuilder.build(
                TestModelDataBuilder.getApiCalculateEvidenceFeeRequest(Boolean.TRUE));

        softly.assertThat(crimeEvidenceDTO.getRepId()).isEqualTo(TestModelDataBuilder.TEST_REP_ID);
        softly.assertThat(crimeEvidenceDTO.getLaaTransactionId()).isEqualTo(TestModelDataBuilder.MEANS_ASSESSMENT_TRANSACTION_ID);
        softly.assertThat(crimeEvidenceDTO.getMagCourtOutcome()).isEqualTo(TestModelDataBuilder.MSG_COURT_OUTCOME);
        softly.assertThat(crimeEvidenceDTO.getEvidenceFee().getFeeLevel()).isEqualTo(EvidenceFeeLevel.LEVEL1.getFeeLevel());
        softly.assertThat(crimeEvidenceDTO.getEvidenceFee().getDescription()).isEqualTo(EvidenceFeeLevel.LEVEL1.getDescription());
        softly.assertThat(crimeEvidenceDTO.getCapitalEvidenceReceivedDate()).isEqualTo(TestModelDataBuilder.CAPITAL_EVIDENCE_RECEIVED_DATE);
        softly.assertThat(crimeEvidenceDTO.getIncomeEvidenceReceivedDate()).isEqualTo(TestModelDataBuilder.INCOME_EVIDENCE_RECEIVED_DATE);
        List<CapitalEvidenceDTO> capitalEvidenceList = crimeEvidenceDTO.getCapitalEvidence();
        softly.assertThat(capitalEvidenceList.size()).isEqualTo(2);
        softly.assertThat(capitalEvidenceList.get(0).getEvidenceType()).isEqualTo("MOCK_TEST");
        softly.assertThat(capitalEvidenceList.get(0).getDateReceived()).isEqualTo(TestModelDataBuilder.CAPITAL_EVIDENCE_RECEIVED_DATE);
        softly.assertThat(capitalEvidenceList.get(1).getEvidenceType()).isEqualTo("MOCK_TEST1");
        softly.assertThat(capitalEvidenceList.get(1).getDateReceived()).isEqualTo(TestModelDataBuilder.INCOME_EVIDENCE_RECEIVED_DATE);
        softly.assertAll();

    }
}