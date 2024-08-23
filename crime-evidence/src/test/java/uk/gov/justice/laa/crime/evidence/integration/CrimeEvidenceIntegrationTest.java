package uk.gov.justice.laa.crime.evidence.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.util.RequestBuilderUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CrimeEvidenceIntegrationTest extends IntegrationTestBase {

    private static final String ERROR_MSG = "Call to service MAAT-API failed.";
    private static final String CALCULATE_EVIDENCE_FEE = EVIDENCE_BASE_URL.concat("/calculate-evidence-fee");

    @Test
    void givenAEmptyOAuthToken_whenCreateAssessmentIsInvoked_thenFailsUnauthorizedAccess() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "{}", CALCULATE_EVIDENCE_FEE, Boolean.FALSE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAValidContent_whenApiResponseIsError_thenCalculateEvidenceFeeIsFails() throws Exception {

        wiremock.stubFor(head(urlEqualTo("/api/internal/v1/assessment/rep-orders/capital/reporder/91919"))
                .willReturn(
                        WireMock.serverError())
                );
        String content = objectMapper.writeValueAsString(TestModelDataBuilder.getApiCalculateEvidenceFeeRequest());
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, content, CALCULATE_EVIDENCE_FEE))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(ERROR_MSG));
    }

    @Test
    void givenValidContent_whenCalculateEvidenceFeeIsInvoked_thenCalculateEvidenceFeeIsSuccess() throws Exception {

        wiremock.stubFor(head(urlEqualTo("/api/internal/v1/assessment/rep-orders/capital/reporder/91919"))
                .willReturn(
                        WireMock.ok().withHeader("Content-Length", "5"))
        );

        String content = objectMapper.writeValueAsString(TestModelDataBuilder.getApiCalculateEvidenceFeeRequest());
        MvcResult result = mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, content, CALCULATE_EVIDENCE_FEE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(TestModelDataBuilder.getApiCalculateEvidenceFeeResponse()));
    }
}