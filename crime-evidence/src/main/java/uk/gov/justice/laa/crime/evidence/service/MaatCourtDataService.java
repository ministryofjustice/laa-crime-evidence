package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.evidence.client.MaatAPIClient;
import uk.gov.justice.laa.crime.evidence.common.Constants;
import uk.gov.justice.laa.crime.evidence.config.ServicesConfiguration;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaatCourtDataService {

    public static final String RESPONSE_STRING = "Response from Court Data API: %s";
    private final MaatAPIClient maatAPIClient;
    private final ServicesConfiguration configuration;

    public Long getRepOrderCapitalByRepId(Integer repId, String laaTransactionId) {

        ResponseEntity<Void> response = maatAPIClient.getApiResponseViaHEAD(
                configuration.getMaatApi().getRepOrderEndpoints().getRepOrderCapitalUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response.getHeaders().getContentLength();
    }
}
