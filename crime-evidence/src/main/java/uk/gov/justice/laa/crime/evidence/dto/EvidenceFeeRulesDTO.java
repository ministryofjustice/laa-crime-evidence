package uk.gov.justice.laa.crime.evidence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceFeeRulesDTO {
    private String emstCode;
    private String allIncomeEvidenceReceived;
    private String allCapitalEvidenceReceived;
    private Long capitalEvidenceItemsLower;
    private Long capitalEvidenceItemsUpper;
}
