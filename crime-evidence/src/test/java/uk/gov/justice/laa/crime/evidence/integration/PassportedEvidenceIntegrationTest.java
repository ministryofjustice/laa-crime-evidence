package uk.gov.justice.laa.crime.evidence.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.util.RequestBuilderUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability
class PassportedEvidenceIntegrationTest extends IntegrationTestBase {

    private static final int PASSPORTED_ASSESSMENT_ID = 999;
    private static final String ENDPOINT_URL = "/api/internal/v1/evidence/passported";
    private static final String MAAT_API_PASSPORTED_EVIDENCE_URL =
            String.format("/api/internal/v1/assessment/passport-assessments/%d/evidence", PASSPORTED_ASSESSMENT_ID);

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidRequest_whenGetEndpointIsCalled_thenPassportEvidenceResponseReturned() throws Exception {
        String response = objectMapper.writeValueAsString(TestModelDataBuilder.getApiPassportEvidenceResponse());

        wiremock.stubFor(get(urlEqualTo(MAAT_API_PASSPORTED_EVIDENCE_URL))
                .willReturn(WireMock.ok()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(response)));

        MvcResult result = mvc.perform(
                        RequestBuilderUtils.buildRequest(HttpMethod.GET, ENDPOINT_URL + "/" + PASSPORTED_ASSESSMENT_ID))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.getResponse().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(result.getResponse().getContentAsString()).isEqualTo(response);
    }

    @Test
    void givenUnauthorisedRequest_whenGetEndpointIsCalled_thenUnauthorisedErrorResponseReturned() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequest(
                        HttpMethod.GET, ENDPOINT_URL + "/" + PASSPORTED_ASSESSMENT_ID, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenErrorResponseFromMaatApi_whenGetEndpointIsCalled_thenInternalServerErrorResponseReturned()
            throws Exception {
        wiremock.stubFor(get(urlEqualTo(MAAT_API_PASSPORTED_EVIDENCE_URL)).willReturn(WireMock.serverError()));

        mvc.perform(RequestBuilderUtils.buildRequest(HttpMethod.GET, ENDPOINT_URL + "/" + PASSPORTED_ASSESSMENT_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("500 Internal Server Error")));
    }
}
