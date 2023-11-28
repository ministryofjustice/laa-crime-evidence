package uk.gov.justice.laa.crime.evidence.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.evidence.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.evidence.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataServiceTest {

    @Mock
    private RestAPIClient maatCourtDataClient;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenAValidRepId_whenGetRepOrderCapitalByRepIdIsInvoked_thenResponseIsReturned() {
        ResponseEntity<Void> expected = new ResponseEntity<>(HttpStatus.OK);
        when(maatCourtDataClient.head(any(), anyMap(), any()))
                .thenReturn(expected);
        maatCourtDataService.getRepOrderCapitalByRepId(TestModelDataBuilder.TEST_REP_ID);
        verify(maatCourtDataClient, atLeastOnce()).head(any(), anyMap(), any());
    }
}