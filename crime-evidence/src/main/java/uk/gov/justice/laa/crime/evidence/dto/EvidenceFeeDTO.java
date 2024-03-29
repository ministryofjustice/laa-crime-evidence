package uk.gov.justice.laa.crime.evidence.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvidenceFeeDTO {
    private String feeLevel;
    private String description;
}
