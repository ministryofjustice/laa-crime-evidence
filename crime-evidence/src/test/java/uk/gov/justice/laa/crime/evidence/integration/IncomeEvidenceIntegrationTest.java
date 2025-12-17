package uk.gov.justice.laa.crime.evidence.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import uk.gov.justice.laa.crime.util.RequestBuilderUtils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

class IncomeEvidenceIntegrationTest extends IntegrationTestBase {

    @Test
    void givenAEmptyOAuthToken_whenCreateEvidenceIsInvoked_thenFailsUnauthorizedAccess() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(
                        HttpMethod.POST, "{}", EVIDENCE_BASE_URL, Boolean.FALSE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAEmptyOAuthToken_whenUpdateEvidenceIsInvoked_thenFailsUnauthorizedAccess() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(
                        HttpMethod.PUT, "{}", EVIDENCE_BASE_URL, Boolean.FALSE))
                .andExpect(status().isUnauthorized());
    }
}
