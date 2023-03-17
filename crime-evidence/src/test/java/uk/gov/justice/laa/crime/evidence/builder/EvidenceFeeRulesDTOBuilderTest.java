package uk.gov.justice.laa.crime.evidence.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeRulesDTO;

@ExtendWith(SoftAssertionsExtension.class)
class EvidenceFeeRulesDTOBuilderTest {

    private static String ALL_INCOME_EVIDENCE_RECEIVED = "Y";
    private static String ALL_CAPITAL_EVIDENCE_RECEIVED = "N";
    private static Long EVIDENCE_LOWER_ITEMS = 0l;
    private static Long EVIDENCE_UPPER_ITEMS = 1l;
    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiCalculateEvidenceFeeRequest_whenBuildIsInvoked_thenCorrectEvidenceFeeDTOFieldsArePopulated() {

        EvidenceFeeRulesDTO evidenceFeeRulesDTO = EvidenceFeeRulesDTOBuilder.build(
                TestModelDataBuilder.EMST_CODE, ALL_INCOME_EVIDENCE_RECEIVED,
                ALL_CAPITAL_EVIDENCE_RECEIVED, EVIDENCE_LOWER_ITEMS, EVIDENCE_UPPER_ITEMS);

        softly.assertThat(evidenceFeeRulesDTO.getEmstCode()).isEqualTo(TestModelDataBuilder.EMST_CODE);
        softly.assertThat(evidenceFeeRulesDTO.getAllIncomeEvidenceReceived()).isEqualTo(ALL_INCOME_EVIDENCE_RECEIVED);
        softly.assertThat(evidenceFeeRulesDTO.getAllCapitalEvidenceReceived()).isEqualTo(ALL_CAPITAL_EVIDENCE_RECEIVED);
        softly.assertThat(evidenceFeeRulesDTO.getCapitalEvidenceItemsLower()).isEqualTo(EVIDENCE_LOWER_ITEMS);
        softly.assertThat(evidenceFeeRulesDTO.getCapitalEvidenceItemsUpper()).isEqualTo(EVIDENCE_UPPER_ITEMS);
        softly.assertAll();

    }
}