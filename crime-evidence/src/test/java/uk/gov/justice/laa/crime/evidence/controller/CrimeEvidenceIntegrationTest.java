package uk.gov.justice.laa.crime.evidence.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.evidence.CrimeEvidenceApplication;
import uk.gov.justice.laa.crime.evidence.config.CrimeEvidenceTestConfiguration;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.util.RequestBuilderUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_IMPLEMENTED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DirtiesContext
@RunWith(SpringRunner.class)
@Import(CrimeEvidenceTestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = CrimeEvidenceApplication.class, webEnvironment = DEFINED_PORT)
class CrimeEvidenceIntegrationTest {

    private static final String CCP_ENDPOINT_URL = "/api/internal/v1/evidence";
    private static final String ERROR_MSG = "Call to service MAAT-API failed.";
    private static final String CALCULATE_EVIDENCE_FEE = CCP_ENDPOINT_URL.concat("/calculate-evidence-fee");

    private MockMvc mvc;
    private static MockWebServer mockMaatCourtDataApi;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @BeforeAll
    public void initialiseMockWebServer() throws IOException {
        mockMaatCourtDataApi = new MockWebServer();
        mockMaatCourtDataApi.start(9999);
    }

    @AfterAll
    protected void shutdownMockWebServer() throws IOException {
        mockMaatCourtDataApi.shutdown();
    }

    @BeforeEach
    public void setup() {
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

        enqueueOAuthResponse();
        String content = objectMapper.writeValueAsString(
                TestModelDataBuilder.getApiCalculateEvidenceFeeInvalidRequest());
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, content, CALCULATE_EVIDENCE_FEE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAValidContent_whenApiResponseIsError_thenCalculateEvidenceFeeIsFails() throws Exception {

        enqueueOAuthResponse();
        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setResponseCode(NOT_IMPLEMENTED.code()));

        String content = objectMapper.writeValueAsString(TestModelDataBuilder.getApiCalculateEvidenceFeeRequest());
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, content, CALCULATE_EVIDENCE_FEE))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(ERROR_MSG));
    }

    @Test
    void givenValidContent_whenCalculateEvidenceFeeIsInvoked_thenCalculateEvidenceFeeIsSuccess() throws Exception {

        enqueueOAuthResponse();
        mockMaatCourtDataApi.enqueue(new MockResponse()
                .setResponseCode(OK.code())
                .setHeader("Content-Length", "5")
        );

        String content = objectMapper.writeValueAsString(TestModelDataBuilder.getApiCalculateEvidenceFeeRequest());
        MvcResult result = mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, content, CALCULATE_EVIDENCE_FEE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AssertionsForClassTypes.assertThat(result.getResponse().getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(TestModelDataBuilder.getApiCalculateEvidenceFeeResponse()));
    }

    private void enqueueOAuthResponse() throws JsonProcessingException {
        Map<String, String> token = Map.of(
                "expires_in", "3600",
                "token_type", "Bearer",
                "access_token", "token"
        );
        MockResponse response = new MockResponse();
        response.setBody(objectMapper.writeValueAsString(token));

        mockMaatCourtDataApi.enqueue(response
                .setResponseCode(OK.code())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody(objectMapper.writeValueAsString(token))
        );
    }
}