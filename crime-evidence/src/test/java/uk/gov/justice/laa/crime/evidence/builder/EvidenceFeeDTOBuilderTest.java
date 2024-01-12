package uk.gov.justice.laa.crime.evidence.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeDTO;
import uk.gov.justice.laa.crime.evidence.model.common.ApiCalculateEvidenceFeeRequest;
import uk.gov.justice.laa.crime.enums.EvidenceFeeLevel;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SoftAssertionsExtension.class)
class EvidenceFeeDTOBuilderTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiCalculateEvidenceFeeRequest_whenBuildIsInvoked_thenCorrectEvidenceFeeDTOFieldsArePopulated() {

        EvidenceFeeDTO evidenceFeeDTO = EvidenceFeeDTOBuilder.build(
                TestModelDataBuilder.getApiCalculateEvidenceFeeRequest(Boolean.TRUE));

        softly.assertThat(evidenceFeeDTO.getFeeLevel()).isEqualTo(EvidenceFeeLevel.LEVEL1.getFeeLevel());
        softly.assertThat(evidenceFeeDTO.getDescription()).isEqualTo(EvidenceFeeLevel.LEVEL1.getDescription());
        softly.assertAll();
    }

    @Test
    void givenEmptyApiCalculateEvidenceFeeRequest_whenBuildIsInvoked_thenNull() {
        ApiCalculateEvidenceFeeRequest apiCalculateEvidenceFeeRequest =
                TestModelDataBuilder.getApiCalculateEvidenceFeeRequest(Boolean.TRUE);
        apiCalculateEvidenceFeeRequest.setEvidenceFee(null);
        assertThat(EvidenceFeeDTOBuilder.build(apiCalculateEvidenceFeeRequest)).isNull();
    }
}