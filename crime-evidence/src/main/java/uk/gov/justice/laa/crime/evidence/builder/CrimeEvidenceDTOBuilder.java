package uk.gov.justice.laa.crime.evidence.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeRequest;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;

import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CrimeEvidenceDTOBuilder {

    public static CrimeEvidenceDTO build(final ApiCalculateEvidenceFeeRequest request) {
        return CrimeEvidenceDTO.builder()
                .repId(request.getRepId())
                .magCourtOutcome(request.getMagCourtOutcome())
                .evidenceFee(EvidenceFeeDTOBuilder.build(request))
                .capitalEvidence(CapitalEvidenceDTOBuilder.build(request))
                .incomeEvidenceReceivedDate(request.getIncomeEvidenceReceivedDate())
                .capitalEvidenceReceivedDate(request.getCapitalEvidenceReceivedDate())
                .emstCode(request.getEmstCode())
                .build();
    }
}
