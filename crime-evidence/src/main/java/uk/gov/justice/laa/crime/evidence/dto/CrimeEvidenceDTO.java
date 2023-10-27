package uk.gov.justice.laa.crime.evidence.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CrimeEvidenceDTO {
    private Integer repId;
    private String magCourtOutcome;
    private EvidenceFeeDTO evidenceFee;
    private List<CapitalEvidenceDTO> capitalEvidence;
    private LocalDateTime incomeEvidenceReceivedDate;
    private LocalDateTime capitalEvidenceReceivedDate;
    private String emstCode;
}
