package uk.gov.justice.laa.crime.evidence.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;

@HttpExchange
public interface MaatCourtDataApiClient {
    @GetExchange("/rep-orders/{repId}/capital-assets/count")
    Integer getCapitalAssetCount(@PathVariable Integer repId);

    @GetExchange("/api/internal/v1/evidence/passported")
    ApiGetPassportedAssessmentResponse getPassportedEvidence(@PathVariable int passportedAssessmentId);
}
