package uk.gov.justice.laa.crime.evidence.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeRulesDTO;

import java.util.stream.Stream;

/**
 * static data migrated from TOGDATA.EVIDENCE_FEE_RULES table
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum EvidenceFeeRules {

    SELF_CASH("SELF-CASH", "Y", "Y", 0, null, EvidenceFeeLevel.LEVEL1),
    SELF_SOT("SELF-SOT", "Y", "Y", 0, null, EvidenceFeeLevel.LEVEL1),
    SELF("SELF", "Y", "Y", 0, null, EvidenceFeeLevel.LEVEL1),
    EMPCDS_LEVEL2("EMPCDS", "Y", "Y", 0, 3, EvidenceFeeLevel.LEVEL2),
    EMPCDS_LEVEL1("EMPCDS", "Y", "Y", 4, null, EvidenceFeeLevel.LEVEL1),
    EMPLOY_LEVEL2("EMPLOY", "Y", "Y", 1, 4, EvidenceFeeLevel.LEVEL2),
    EMPLOY_LEVEL1("EMPLOY", "Y", "Y", 5, null, EvidenceFeeLevel.LEVEL1),
    EMPLOYED_CASH_LEVEL2("EMPLOYED-CASH", "Y", "Y", 1, 4, EvidenceFeeLevel.LEVEL2),
    EMPLOYED_CASH_LEVEL1("EMPLOYED-CASH", "Y", "Y", 5, null, EvidenceFeeLevel.LEVEL1),
    NONPASS_LEVEL2("NONPASS", "Y", "Y", 1, 4, EvidenceFeeLevel.LEVEL2),
    NONPASS_LEVEL1("NONPASS", "Y", "Y", 5, null, EvidenceFeeLevel.LEVEL1);


    @JsonPropertyDescription("Specifies the Evidence Fee Rules")
    private final String emstCode;
    private final String allIncomeEvidenceReceived;
    private final String allCapitalEvidenceReceived;
    private final Integer capitalEvidenceItemsLower;
    private final Integer capitalEvidenceItemsUpper;
    private final EvidenceFeeLevel evidenceFeeLevel;

    public static EvidenceFeeRules getFrom(EvidenceFeeRulesDTO evidenceFeeRulesDTO) {
        if (validateEvidenceFeeRulesDTO(evidenceFeeRulesDTO)) return null;

        return Stream.of(EvidenceFeeRules.values())
                .filter(f -> isEquals(f, evidenceFeeRulesDTO))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("EvidenceFeeRules with value: %s does not exist.", evidenceFeeRulesDTO)));
    }

    private static boolean validateEvidenceFeeRulesDTO(EvidenceFeeRulesDTO evidenceFeeRulesDTO) {
        return evidenceFeeRulesDTO == null || (
                StringUtils.isBlank(evidenceFeeRulesDTO.getEmstCode()) ||
                        StringUtils.isBlank(evidenceFeeRulesDTO.getAllIncomeEvidenceReceived()) ||
                        StringUtils.isBlank(evidenceFeeRulesDTO.getAllCapitalEvidenceReceived()) ||
                        evidenceFeeRulesDTO.getCapitalEvidenceItemsLower() == null);
    }

    private static boolean isEquals(EvidenceFeeRules evidenceFeeRules, EvidenceFeeRulesDTO evidenceFeeRulesDTO) {
        return evidenceFeeRules.emstCode.equals(evidenceFeeRulesDTO.getEmstCode()) &&
                evidenceFeeRules.allIncomeEvidenceReceived.equals(evidenceFeeRulesDTO.getAllIncomeEvidenceReceived()) &&
                evidenceFeeRules.allCapitalEvidenceReceived.equals(evidenceFeeRulesDTO.getAllCapitalEvidenceReceived()) &&
                evidenceFeeRules.capitalEvidenceItemsLower <= evidenceFeeRulesDTO.getCapitalEvidenceItemsLower() &&
                (evidenceFeeRules.capitalEvidenceItemsUpper == null || evidenceFeeRules.capitalEvidenceItemsUpper >= evidenceFeeRulesDTO.getCapitalEvidenceItemsUpper());
    }
}