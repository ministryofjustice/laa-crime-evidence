package uk.gov.justice.laa.crime.evidence.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.evidence.config.ServicesConfiguration;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeansAssessmentApiService {
    @Qualifier("cmaApiClient")
    private final RestAPIClient cmaApiClient;
    private final ServicesConfiguration configuration;

    public ApiGetMeansAssessmentResponse find(Integer assessmentId) {
        ApiGetMeansAssessmentResponse response = cmaApiClient.get(
            new ParameterizedTypeReference<>() {
            },
            configuration.getCmaApi().getEndpoints().getFindUrl(),
            assessmentId
        );

        return response;
    }

    // TODO: Need to update the request object here to match the one that has been created in crime-commons.
    // TODO: Also need to check if this response type is correct.
    public ApiMeansAssessmentResponse update(ApiUpdateMeansAssessmentRequest request) {
        ApiMeansAssessmentResponse response = cmaApiClient.put(
            request,
            new ParameterizedTypeReference<>() {
            },
            configuration.getCmaApi().getEndpoints().getUpdateUrl(),
            Map.of()
        );

        return response;

    }
}
