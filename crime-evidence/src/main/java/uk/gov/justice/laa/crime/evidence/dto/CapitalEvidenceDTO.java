package uk.gov.justice.laa.crime.evidence.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CapitalEvidenceDTO {
    private String evidenceType;
    private LocalDateTime dateReceived;
}
