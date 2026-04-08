package uk.gov.justice.laa.crime.evidence.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder.getApiPassportEvidenceResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.evidence.client.MaatCourtDataApiClient;

@ExtendWith(MockitoExtension.class)
class PassportedEvidenceServiceTest {

    private static final int PASSPORTED_ASSESSMENT_ID = 999;

    @Mock
    private MaatCourtDataApiClient maatCourtDataApiClient;

    @InjectMocks
    private PassportedEvidenceService passportedEvidenceService;

    @Test
    void givenValidId_whenGetPassportedEvidenceIsInvoked_thenPassportedEvidenceResponseReturned() {
        ApiGetPassportEvidenceResponse expectedResponse = getApiPassportEvidenceResponse();
        when(maatCourtDataApiClient.getPassportedEvidence(anyInt())).thenReturn(expectedResponse);

        ApiGetPassportEvidenceResponse actualResponse = passportedEvidenceService.getPassportedEvidence(PASSPORTED_ASSESSMENT_ID);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
