package uk.gov.justice.laa.crime.evidence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.crowncourt.config.WireMockServerConfig;
import uk.gov.justice.laa.crime.evidence.CrimeEvidenceApplication;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true",
        classes = {CrimeEvidenceApplication.class, WireMockServerConfig.class}, webEnvironment = DEFINED_PORT)
@RunWith(SpringRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class CrimeEvidenceIntegrationTest {

    private static final String CLIENT_SECRET = "secret";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String CLIENT_ID = "test-client";
    private static final String SCOPE_READ_WRITE = "READ_WRITE";
    private static final String CCP_ENDPOINT_URL = "/api/internal/v1/evidence";

    private static final String MAAT_COURT_API_ENDPOINT_URL = "/api/internal/v1/assessment/.*";

    private static final String ERROR_MSG = "Call to service MAAT-API failed.";

    private static final String CALCULATE_EVIDENCE_FEE = "/calculate-evidence-fee";

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WireMockServer webServer;


    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).addFilter(springSecurityFilterChain).build();
    }


    private MockHttpServletRequestBuilder buildRequestGivenContent(HttpMethod method, String url, String content) throws Exception {
        return buildRequestGivenContent(method, url, content, true);
    }

    private MockHttpServletRequestBuilder buildRequestGivenContent(HttpMethod method, String url, String content, boolean withAuth) throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.request(method, CCP_ENDPOINT_URL.concat(url))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);
        if (withAuth) {
            final String accessToken = obtainAccessToken();
            requestBuilder.header("Authorization", "Bearer " + accessToken);
        }
        return requestBuilder;
    }

    private String obtainAccessToken() throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", CLIENT_CREDENTIALS);
        params.add("scope", SCOPE_READ_WRITE);

        ResultActions result = mvc.perform(post("/oauth2/token")
                        .params(params)
                        .with(httpBasic(CLIENT_ID, CLIENT_SECRET)))
                .andExpect(status().isOk());
        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

    @Test
    void givenAEmptyContent_whenProcessRepOrderIsInvoked_thenFailsBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, CALCULATE_EVIDENCE_FEE, "{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenCreateAssessmentIsInvoked_thenFailsUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, CALCULATE_EVIDENCE_FEE, "{}", Boolean.FALSE))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void givenInvalidContent_whenCalculateEvidenceFeeIsInvoked_thenFailsBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, CALCULATE_EVIDENCE_FEE, objectMapper.writeValueAsString(
                        TestModelDataBuilder.getApiCalculateEvidenceFeeInvalidRequest())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAValidContent_whenApiResponseIsError_thenCalculateEvidenceFeeIsFails() throws Exception {

        webServer.stubFor(head(urlPathMatching(MAAT_COURT_API_ENDPOINT_URL)).willReturn(aResponse()
                .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, CALCULATE_EVIDENCE_FEE,
                        objectMapper.writeValueAsString(TestModelDataBuilder.getApiCalculateEvidenceFeeRequest())))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(ERROR_MSG));
    }

    @Test
    void givenValidContent_whenCalculateEvidenceFeeIsInvoked_thenCalculateEvidenceFeeIsSuccess() throws Exception {
        webServer.stubFor(head(urlPathMatching(MAAT_COURT_API_ENDPOINT_URL)).willReturn(aResponse()
                .withHeader("Content-Length", "5")));

        MvcResult result = mvc.perform(buildRequestGivenContent(HttpMethod.POST, CALCULATE_EVIDENCE_FEE,
                        objectMapper.writeValueAsString(TestModelDataBuilder.getApiCalculateEvidenceFeeRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AssertionsForClassTypes.assertThat(result.getResponse().getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(TestModelDataBuilder.getApiCalculateEvidenceFeeResponse()));
    }

    @AfterAll
    public void cleanUp() {
        webServer.shutdown();
    }

}