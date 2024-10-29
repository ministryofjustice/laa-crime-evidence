package uk.gov.justice.laa.crime.evidence.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.evidence.dto.CreateEvidenceDTO;

import java.math.BigDecimal;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateEvidenceDTOBuilder {

    public static CreateEvidenceDTO build(final ApiCreateIncomeEvidenceRequest request) {
        return CreateEvidenceDTO.builder()
            .magCourtOutcome(request.getMagCourtOutcome())
            .applicantDetails(request.getApplicantDetails())
            .applicantPensionAmount(request.getApplicantPensionAmount() != null ? request.getApplicantPensionAmount() : BigDecimal.ZERO)
            .partnerDetails(request.getPartnerDetails())
            .partnerPensionAmount(request.getPartnerPensionAmount() != null ? request.getPartnerPensionAmount() : BigDecimal.ZERO)
            .build();
    }
}
