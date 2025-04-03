package uk.gov.justice.laa.crime.evidence.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.evidence.client.MaatCourtDataApiClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataService {

    private final MaatCourtDataApiClient maatCourtDataApiClient;
    private static final String SERVICE_NAME = "maatCourtDataService";
    private static final String RESPONSE_STRING = "Response from Court Data API: %s";

    @Retry(name = SERVICE_NAME)
    public Integer getCapitalAssetCount(Integer repId) {
        log.debug("Request to retrieve capital asset count for repId: {}", repId);
        Integer response = maatCourtDataApiClient.getCapitalAssetCount(repId);
        log.debug(String.format(RESPONSE_STRING, response)); // Do we need these if we logging req and resp in config???
        return response;
    }
}
