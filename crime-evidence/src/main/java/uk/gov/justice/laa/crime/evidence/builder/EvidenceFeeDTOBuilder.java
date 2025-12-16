package uk.gov.justice.laa.crime.evidence.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeRequest;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceFeeDTO;

import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EvidenceFeeDTOBuilder {

    public static EvidenceFeeDTO build(final ApiCalculateEvidenceFeeRequest request) {
        if (request.getEvidenceFee() != null) {
            return EvidenceFeeDTO.builder()
                    .feeLevel(request.getEvidenceFee().getFeeLevel())
                    .description(request.getEvidenceFee().getDescription())
                    .build();
        }
        return null;
    }
}
