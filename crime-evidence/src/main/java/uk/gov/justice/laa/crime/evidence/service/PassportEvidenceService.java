package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.evidence.client.MaatDataApiClient;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassportEvidenceService {

    private final MaatDataApiClient maatDataApiClient;

    private static final String RESPONSE_STRING = "Response from Court Data API: %s";

    public ApiGetPassportEvidenceResponse getPassportEvidence(int passportAssessmentId) {
        log.debug("Request to retrieve evidence for passport assessment: {}", passportAssessmentId);
        ApiGetPassportEvidenceResponse response = maatDataApiClient.getPassportEvidence(passportAssessmentId);
        log.debug(String.format(RESPONSE_STRING, response));
        return response;
    }
}
