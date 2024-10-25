package uk.gov.justice.laa.crime.evidence.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.evidence.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.evidence.config.ServicesConfiguration;

@ExtendWith(MockitoExtension.class)
class MeansAssessmentApiServiceTest {
    @Mock
    private RestAPIClient cmaApiClient;

    @InjectMocks
    private MeansAssessmentApiService meansAssessmentApiService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenAValidFinancialAssessmentId_whenFindIsInvoked_thenResponseIsReturned() {
        when(cmaApiClient.get(any(), any(), eq(1))).thenReturn(new ApiGetMeansAssessmentResponse());
        meansAssessmentApiService.find(1);
        verify(cmaApiClient, atLeastOnce()).get(any(), any(), eq(1));
    }

    @Test
    void givenAValidRequest_whenUpdateIsInvoked_thenResponseIsReturned() {
        ApiUpdateMeansAssessmentRequest request = new ApiUpdateMeansAssessmentRequest()
            .withFinancialAssessmentId(2)
            .withMagCourtOutcome(MagCourtOutcome.APPEAL_TO_CC);

        when(cmaApiClient.put(eq(request), any(), any(), any())).thenReturn(new ApiMeansAssessmentResponse());
        meansAssessmentApiService.update(request);
        verify(cmaApiClient, atLeastOnce()).put(eq(request), any(), any(), any());
    }
}
