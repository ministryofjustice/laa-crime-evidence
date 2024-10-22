package uk.gov.justice.laa.crime.evidence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class EvidenceReceivedResultDTO {
    private boolean evidenceReceived;
    private int incomeEvidenceRequiredId;
    private int minimumEvidenceItemsRequired;
}
