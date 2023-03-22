package uk.gov.justice.laa.crime.evidence.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.dto.CapitalEvidenceDTO;

import java.util.List;

@ExtendWith(SoftAssertionsExtension.class)
class CapitalEvidenceDTOBuilderTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiCalculateEvidenceFeeRequest_whenBuildIsInvoked_thenCorrectCapitalEvidenceDTOFieldsArePopulated() {

        List<CapitalEvidenceDTO> capitalEvidenceList = CapitalEvidenceDTOBuilder.build(
                TestModelDataBuilder.getApiCalculateEvidenceFeeRequest(Boolean.TRUE));

        softly.assertThat(capitalEvidenceList.size()).isEqualTo(2);
        softly.assertThat(capitalEvidenceList.get(0).getEvidenceType()).isEqualTo("MOCK_TEST");
        softly.assertThat(capitalEvidenceList.get(0).getDateReceived()).isEqualTo(TestModelDataBuilder.CAPITAL_EVIDENCE_RECEIVED_DATE);
        softly.assertThat(capitalEvidenceList.get(1).getEvidenceType()).isEqualTo("MOCK_TEST1");
        softly.assertThat(capitalEvidenceList.get(1).getDateReceived()).isEqualTo(TestModelDataBuilder.INCOME_EVIDENCE_RECEIVED_DATE);
        softly.assertAll();

    }

}