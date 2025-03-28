package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
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

    public Integer getCapitalAssetCount(Integer repId) {
        Integer response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getRepOrderEndpoints().getGetCapitalAssetCountUrl(),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }
}
