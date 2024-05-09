package uk.gov.justice.laa.crime.evidence.staticdata.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class IncomeEvidenceTest {

    @Test
    void givenABlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(IncomeEvidence.getFrom(null)).isNull();
    }

    @Test
    void valueOfCurrentStatusFromString_nullParameter_ReturnsNull() {
        assertThatThrownBy(
                () -> IncomeEvidence.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGetFrom() {
        IncomeEvidence result = IncomeEvidence.getFrom("SIGNATURE");
        assertThat(result).isEqualTo(IncomeEvidence.SIGNATURE);
    }

    @Test
    void testValues() {
        IncomeEvidence[] result = IncomeEvidence.values();
        assertThat(result).isEqualTo(new IncomeEvidence[]{
                IncomeEvidence.SIGNATURE,
                IncomeEvidence.CDS15,
                IncomeEvidence.FREEZING,
                IncomeEvidence.RESTRAINING,
                IncomeEvidence.CONFISCATION,
                IncomeEvidence.OTHER_ADHOC,
                IncomeEvidence.EMP_LETTER_ADHOC,
                IncomeEvidence.WAGE_SLIP_ADHOC,
                IncomeEvidence.NINO,
                IncomeEvidence.ACCOUNTS,
                IncomeEvidence.OTHER_BUSINESS,
                IncomeEvidence.CASH_BOOK,
                IncomeEvidence.WAGE_SLIP,
                IncomeEvidence.BANK_STATEMENT,
                IncomeEvidence.TAX_RETURN,
                IncomeEvidence.EMP_LETTER,
                IncomeEvidence.OTHER
        });
    }

    @Test
    void testValueOf() {
        IncomeEvidence result = IncomeEvidence.valueOf("SIGNATURE");
        assertThat(result).isEqualTo(IncomeEvidence.SIGNATURE);
    }
}

