package uk.gov.justice.laa.crime.evidence.staticdata.enums;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.enums.EvidenceFeeLevel;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeRulesDTO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class EvidenceFeeRulesTest {

    @Test
    void givenABlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(EvidenceFeeRules.getFrom(null)).isNull();
    }

    @Test
    void valueOfCurrentStatusFromString_nullParameter_ReturnsNull() {
        assertThatThrownBy(
                () -> EvidenceFeeLevel.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGetFrom() {
        EvidenceFeeRules result = EvidenceFeeRules.getFrom(
                new EvidenceFeeRulesDTO("SELF-CASH", "Y",
                        "Y", Long.valueOf(0), Long.valueOf(0)));
        assertThat(result).isEqualTo(EvidenceFeeRules.SELF_CASH);
    }

    @Test
    void testValues() {
        EvidenceFeeRules[] result = EvidenceFeeRules.values();
        assertThat(result).isEqualTo(new EvidenceFeeRules[]{
                EvidenceFeeRules.SELF_CASH,
                EvidenceFeeRules.SELF_SOT,
                EvidenceFeeRules.SELF,
                EvidenceFeeRules.EMPCDS_LEVEL2,
                EvidenceFeeRules.EMPCDS_LEVEL1,
                EvidenceFeeRules.EMPLOY_LEVEL2,
                EvidenceFeeRules.EMPLOY_LEVEL1,
                EvidenceFeeRules.EMPLOYED_CASH_LEVEL2,
                EvidenceFeeRules.EMPLOYED_CASH_LEVEL1,
                EvidenceFeeRules.NONPASS_LEVEL2,
                EvidenceFeeRules.NONPASS_LEVEL1
        });
    }

    @Test
    void testValueOf() {
        EvidenceFeeRules result = EvidenceFeeRules.valueOf("SELF_CASH");
        assertThat(result).isEqualTo(EvidenceFeeRules.SELF_CASH);
    }
}

