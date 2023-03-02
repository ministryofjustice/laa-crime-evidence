package uk.gov.justice.laa.crime.evidence.staticdata.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeRulesDTO;

class EvidenceFeeRulesTest {

    @Test
    void testGetFrom() {
        EvidenceFeeRules result = EvidenceFeeRules.getFrom(
                new EvidenceFeeRulesDTO("SELF-CASH", "Y",
                        "Y", Integer.valueOf(0),
                        Integer.valueOf(0)));
        Assertions.assertEquals(EvidenceFeeRules.SELF_CASH, result);
    }

    @Test
    void testValues() {
        EvidenceFeeRules[] result = EvidenceFeeRules.values();
        Assertions.assertArrayEquals(new EvidenceFeeRules[]{
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
                EvidenceFeeRules.NONPASS_LEVEL1,

        }, result);
    }

    @Test
    void testValueOf() {
        EvidenceFeeRules result = EvidenceFeeRules.valueOf("LEVEL1");
        Assertions.assertEquals(EvidenceFeeRules.SELF_CASH, result);
    }
}

