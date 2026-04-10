package uk.gov.justice.laa.crime.evidence.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.evidence.client.MaatDataApiClient;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaatDataServiceTest {

    @Mock
    private MaatDataApiClient maatDataApiClient;

    @InjectMocks
    private MaatDataService maatDataService;

    @Test
    void givenAValidRepId_whenGetCapitalAssetCountIsInvoked_thenResponseIsReturned() {
        when(maatDataApiClient.getCapitalAssetCount(any())).thenReturn(5);
        assertThat(maatDataService.getCapitalAssetCount(TestModelDataBuilder.TEST_REP_ID))
                .isEqualTo(5);
    }
}
