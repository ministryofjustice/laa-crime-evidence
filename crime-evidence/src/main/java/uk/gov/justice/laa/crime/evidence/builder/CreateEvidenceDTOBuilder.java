package uk.gov.justice.laa.crime.evidence.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.evidence.dto.CreateEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateEvidenceDTOBuilder {

    public static CreateEvidenceDTO build(final ApiCreateIncomeEvidenceRequest request) {
        return CreateEvidenceDTO.builder()
            .magCourtOutcome(request.getMagCourtOutcome())
            .applicantDetails(request.getApplicantDetails())
            .applicantPensionAmount(request.getApplicantPensionAmount() != null ? request.getApplicantPensionAmount().doubleValue() : 0)
            .partnerDetails(request.getPartnerDetails())
            .partnerPensionAmount(request.getPartnerPensionAmount() != null ? request.getPartnerPensionAmount().doubleValue() : 0)
            .build();
    }
}
