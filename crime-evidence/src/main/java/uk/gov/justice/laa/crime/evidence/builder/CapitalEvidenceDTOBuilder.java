package uk.gov.justice.laa.crime.evidence.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.evidence.dto.CapitalEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.model.common.ApiCalculateEvidenceFeeRequest;

import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CapitalEvidenceDTOBuilder {

    public static List<CapitalEvidenceDTO> build(final ApiCalculateEvidenceFeeRequest request) {
        return request.getCapitalEvidence().stream().map(x -> CapitalEvidenceDTO.builder()
                .evidenceType(x.getEvidenceType())
                .dateReceived(x.getDateReceived()).build()).toList();
    }
}
