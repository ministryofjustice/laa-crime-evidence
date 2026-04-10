package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.evidence.client.MaatDataApiClient;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatDataService {

    private final MaatDataApiClient maatDataApiClient;
    private static final String RESPONSE_STRING = "Response from Court Data API: %s";

    public Integer getCapitalAssetCount(Integer repId) {
        log.debug("Request to retrieve capital asset count for repId: {}", repId);
        Integer response = maatDataApiClient.getCapitalAssetCount(repId);
        log.debug(String.format(RESPONSE_STRING, response));
        return response;
    }
}
