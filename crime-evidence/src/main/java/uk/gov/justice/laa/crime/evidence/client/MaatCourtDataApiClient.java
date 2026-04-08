package uk.gov.justice.laa.crime.evidence.client;

import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface MaatCourtDataApiClient {
    @GetExchange("/rep-orders/{repId}/capital-assets/count")
    Integer getCapitalAssetCount(@PathVariable Integer repId);

    @GetExchange("/passport-assessments/{passportedAssessmentId}/evidence")
    ApiGetPassportEvidenceResponse getPassportedEvidence(@PathVariable int passportedAssessmentId);
}
