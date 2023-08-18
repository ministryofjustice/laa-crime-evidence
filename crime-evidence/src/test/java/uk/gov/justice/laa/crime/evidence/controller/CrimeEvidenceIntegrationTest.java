package uk.gov.justice.laa.crime.evidence.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.evidence.CrimeEvidenceApplication;
import uk.gov.justice.laa.crime.evidence.config.CrimeEvidenceTestConfiguration;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.util.RequestBuilderUtils;

import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(CrimeEvidenceTestConfiguration.class)
@SpringBootTest(classes = CrimeEvidenceApplication.class, webEnvironment = DEFINED_PORT)
class CrimeEvidenceIntegrationTest {

    private static final String CCP_ENDPOINT_URL = "/api/internal/v1/evidence";
    private static final String ERROR_MSG = "Call to service MAAT-API failed.";
    private static final String CALCULATE_EVIDENCE_FEE = CCP_ENDPOINT_URL.concat("/calculate-evidence-fee");

    private MockMvc mvc;
    private static final WireMockServer wiremock = new WireMockServer(9999);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @AfterEach
    void after() {
        wiremock.resetAll();
    }

    @AfterAll
    static void clean() {
        wiremock.shutdown();
    }

    @BeforeAll
    void startWiremock() {
        wiremock.start();
    }
    @BeforeEach
    public void setup() throws JsonProcessingException {
        stubForOAuth();
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    @Test
    void givenAEmptyContent_whenProcessRepOrderIsInvoked_thenFailsBadRequest() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "{}", CALCULATE_EVIDENCE_FEE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenCreateAssessmentIsInvoked_thenFailsUnauthorizedAccess() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "{}", CALCULATE_EVIDENCE_FEE, Boolean.FALSE))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void givenInvalidContent_whenCalculateEvidenceFeeIsInvoked_thenFailsBadRequest() throws Exception {
        String content = objectMapper.writeValueAsString(
                TestModelDataBuilder.getApiCalculateEvidenceFeeInvalidRequest());
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, content, CALCULATE_EVIDENCE_FEE))
                .andExpect(status().isBadRequest());
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

    private void stubForOAuth() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> token = Map.of(
                "expires_in", 3600,
                "token_type", "Bearer",
                "access_token", UUID.randomUUID()
        );

        wiremock.stubFor(
                post("/oauth2/token").willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(mapper.writeValueAsString(token))
                )
        );
    }
}