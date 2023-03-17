package uk.gov.justice.laa.crime.evidence.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.model.common.ApiCalculateEvidenceFeeRequest;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CrimeEvidenceDTOBuilder {

    public static CrimeEvidenceDTO build(final ApiCalculateEvidenceFeeRequest request) {
        CrimeEvidenceDTO.CrimeEvidenceDTOBuilder builder = CrimeEvidenceDTO.builder()
                .laaTransactionId(request.getLaaTransactionId())
                .repId(request.getRepId())
                .magCourtOutcome(request.getMagCourtOutcome())
                .evidenceFee(EvidenceFeeDTOBuilder.build(request))
                .capitalEvidence(CapitalEvidenceDTOBuilder.build(request))
                .incomeEvidenceReceivedDate(request.getIncomeEvidenceReceivedDate())
                .capitalEvidenceReceivedDate(request.getCapitalEvidenceReceivedDate())
                .emstCode(request.getEmstCode());
        return builder.build();
    }
}
