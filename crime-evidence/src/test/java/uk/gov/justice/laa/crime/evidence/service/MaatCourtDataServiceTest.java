package uk.gov.justice.laa.crime.evidence.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.evidence.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.evidence.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataServiceTest {

    @Mock
    private RestAPIClient maatCourtDataClient;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenAValidRepId_whenGetCapitalAssetCountIsInvoked_thenResponseIsReturned() {
        when(maatCourtDataClient.get(any(), any(), any())).thenReturn(5);
        assertThat(maatCourtDataService.getCapitalAssetCount(TestModelDataBuilder.TEST_REP_ID))
                .isEqualTo(5);
    }
}