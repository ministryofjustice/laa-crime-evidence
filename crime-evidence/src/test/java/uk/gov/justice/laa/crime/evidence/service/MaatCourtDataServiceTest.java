package uk.gov.justice.laa.crime.evidence.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.evidence.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataServiceTest {

    @Mock
    private MaatCourtDataApiClient maatCourtDataApiClient;
    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Test
    void givenAValidRepId_whenGetCapitalAssetCountIsInvoked_thenResponseIsReturned() {
        when(maatCourtDataApiClient.getCapitalAssetCount(any())).thenReturn(5);
        assertThat(maatCourtDataService.getCapitalAssetCount(TestModelDataBuilder.TEST_REP_ID))
                .isEqualTo(5);
    }
}