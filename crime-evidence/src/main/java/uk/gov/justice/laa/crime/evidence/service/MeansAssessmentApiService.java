package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.ApiGetMeansAssessmentResponse;
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
}
