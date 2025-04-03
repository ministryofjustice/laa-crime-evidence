package uk.gov.justice.laa.crime.evidence.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

public interface MaatCourtDataApiClient {
    @GetExchange("/rep-orders/{repId}/capital-assets/count")
    Mono<Integer> getCapitalAssetCount(@PathVariable Integer repId);
}
