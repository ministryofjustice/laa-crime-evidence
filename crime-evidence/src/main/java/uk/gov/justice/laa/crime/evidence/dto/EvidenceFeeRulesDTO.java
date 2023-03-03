package uk.gov.justice.laa.crime.evidence.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceFeeRulesDTO {
    private String emstCode;
    private String allIncomeEvidenceReceived;
    private String allCapitalEvidenceReceived;
    private Integer capitalEvidenceItemsLower;
    private Integer capitalEvidenceItemsUpper;

}
