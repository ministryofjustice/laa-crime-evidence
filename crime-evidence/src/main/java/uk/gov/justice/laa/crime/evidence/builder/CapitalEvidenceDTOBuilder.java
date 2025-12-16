package uk.gov.justice.laa.crime.evidence.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeRequest;
import uk.gov.justice.laa.crime.evidence.dto.CapitalEvidenceDTO;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CapitalEvidenceDTOBuilder {

    public static List<CapitalEvidenceDTO> build(final ApiCalculateEvidenceFeeRequest request) {
        return request.getCapitalEvidence().stream()
                .map(x -> CapitalEvidenceDTO.builder()
                        .evidenceType(x.getEvidenceType())
                        .dateReceived(x.getDateReceived())
                        .build())
                .toList();
    }
}
