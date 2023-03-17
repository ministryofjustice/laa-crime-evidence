package uk.gov.justice.laa.crime.evidence.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeRulesDTO;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EvidenceFeeRulesDTOBuilder {

    public static EvidenceFeeRulesDTO build(String emstCode,
                                            String allIncomeEvidenceReceived,
                                            String allCapitalEvidenceReceived,
                                            Long capitalEvidenceItemsLower,
                                            Long capitalEvidenceItemsUpper
    ) {
        EvidenceFeeRulesDTO.EvidenceFeeRulesDTOBuilder builder = EvidenceFeeRulesDTO.builder()
                .emstCode(emstCode)
                .allIncomeEvidenceReceived(allIncomeEvidenceReceived)
                .allCapitalEvidenceReceived(allCapitalEvidenceReceived)
                .capitalEvidenceItemsLower(capitalEvidenceItemsLower)
                .capitalEvidenceItemsUpper(capitalEvidenceItemsUpper);
        return builder.build();
    }
}
