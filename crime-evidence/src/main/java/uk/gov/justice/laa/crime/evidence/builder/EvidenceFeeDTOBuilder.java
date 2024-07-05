package uk.gov.justice.laa.crime.evidence.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeDTO;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeRequest;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EvidenceFeeDTOBuilder {

    public static EvidenceFeeDTO build(final ApiCalculateEvidenceFeeRequest request) {
        if (request.getEvidenceFee() != null) {
            return EvidenceFeeDTO.builder()
                    .feeLevel(request.getEvidenceFee().getFeeLevel())
                    .description(request.getEvidenceFee().getDescription()).build();
        }
        return null;
    }
}
