package uk.gov.justice.laa.crime.evidence.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import uk.gov.justice.laa.crime.common.model.meansassessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.evidence.config.ServicesConfiguration;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeansAssessmentApiService {
    @Qualifier("cmaApiClient")
    private final RestAPIClient cmaApiClient;
    private final ServicesConfiguration configuration;
    private static final String REQUEST_STRING = "Request to Means Assessment Api: {}";
    private static final String RESPONSE_STRING = "Response from Means Assessment Api: {}";

    public ApiGetMeansAssessmentResponse find(Integer assessmentId) {
        log.debug(REQUEST_STRING, assessmentId);
        ApiGetMeansAssessmentResponse response = cmaApiClient.get(
            new ParameterizedTypeReference<>() {
            },
            configuration.getCmaApi().getEndpoints().getFindUrl(),
            assessmentId
        );
        log.debug(RESPONSE_STRING, response);

        return response;
    }

    public ApiMeansAssessmentResponse update(ApiUpdateMeansAssessmentRequest request) {
        log.debug(REQUEST_STRING, request);
        ApiMeansAssessmentResponse response = cmaApiClient.put(
            request,
            new ParameterizedTypeReference<>() {
            },
            configuration.getCmaApi().getEndpoints().getUpdateUrl(),
            Map.of()
        );
        log.debug(RESPONSE_STRING, response);

        return response;

    }
}
