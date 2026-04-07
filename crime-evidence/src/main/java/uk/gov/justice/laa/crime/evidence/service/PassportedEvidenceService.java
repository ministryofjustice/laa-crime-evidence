package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.evidence.client.MaatCourtDataApiClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassportedEvidenceService {

    private final MaatCourtDataApiClient maatCourtDataApiClient;
    private static final String RESPONSE_STRING = "Response from Court Data API: %s";

    public ApiGetPassportedAssessmentResponse getPassportedEvidence(int passportedAssessmentId) {
        log.debug("Request to retrieve evidence for passported assessment: {}", passportedAssessmentId);
        ApiGetPassportedAssessmentResponse response = maatCourtDataApiClient.getPassportedEvidence(passportedAssessmentId);
        log.debug(String.format(RESPONSE_STRING, response));
        return response;
    }

}
