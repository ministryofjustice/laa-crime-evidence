package uk.gov.justice.laa.crime.evidence.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder.getApiPassportEvidenceResponse;

import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.evidence.client.MaatDataApiClient;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PassportEvidenceServiceTest {

    @Mock
    private MaatDataApiClient maatDataApiClient;

    @InjectMocks
    private PassportEvidenceService passportEvidenceService;

    @Test
    void givenValidId_whenGetPassportEvidenceIsInvoked_thenPassportEvidenceResponseReturned() {
        ApiGetPassportEvidenceResponse expectedResponse = getApiPassportEvidenceResponse();
        when(maatDataApiClient.getPassportEvidence(anyInt())).thenReturn(expectedResponse);

        ApiGetPassportEvidenceResponse actualResponse =
                passportEvidenceService.getPassportEvidence(TestModelDataBuilder.PASSPORT_ASSESSMENT_ID);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
