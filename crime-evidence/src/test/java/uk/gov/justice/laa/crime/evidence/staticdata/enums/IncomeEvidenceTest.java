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
    void givenAInValidValue_whenGetFromIsInvoked_thenReturnsException() {
        assertThatThrownBy(
                () -> IncomeEvidence.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenAValidValue_whenGetFromIsInvoked_thenReturnsValue() {
        IncomeEvidence result = IncomeEvidence.getFrom("SIGNATURE");
        assertThat(result).isEqualTo(IncomeEvidence.SIGNATURE);
    }

    @Test
    void givenAValidValue_whenGetFromIsInvoked_thenCorrectValueIsReturned() {
        IncomeEvidence result = IncomeEvidence.valueOf("SIGNATURE");
        assertThat(result).isEqualTo(IncomeEvidence.SIGNATURE);
    }
}

