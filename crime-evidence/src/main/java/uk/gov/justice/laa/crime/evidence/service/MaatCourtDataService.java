package uk.gov.justice.laa.crime.evidence.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.evidence.config.ServicesConfiguration;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataService {

    @Qualifier("maatApiClient")
    private final RestAPIClient maatAPIClient;
    private final ServicesConfiguration configuration;
    private static final String RESPONSE_STRING = "Response from Court Data API: %s";

    public Long getCapitalAssetCount(Integer repId) {

        ResponseEntity<Void> response = maatAPIClient.head(
                configuration.getMaatApi().getRepOrderEndpoints().getGetCapitalAssetCountUrl(),
                Map.of(),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response.getHeaders().getContentLength();
    }
}
