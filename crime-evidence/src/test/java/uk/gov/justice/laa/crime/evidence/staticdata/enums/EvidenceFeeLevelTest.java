package uk.gov.justice.laa.crime.evidence.staticdata.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvidenceFeeLevelTest {

    @Test
    void testGetFrom() {
        EvidenceFeeLevel result = EvidenceFeeLevel.getFrom("LEVEL1");
        assertThat(result).isEqualTo(EvidenceFeeLevel.LEVEL1);
    }

    @Test
    void testValues() {
        EvidenceFeeLevel[] result = EvidenceFeeLevel.values();
        assertThat(result).isEqualTo(new EvidenceFeeLevel[]{EvidenceFeeLevel.LEVEL1, EvidenceFeeLevel.LEVEL2});
    }

    @Test
    void testValueOf() {
        EvidenceFeeLevel result = EvidenceFeeLevel.valueOf("LEVEL1");
        assertThat(result).isEqualTo(EvidenceFeeLevel.LEVEL1);
    }
}

