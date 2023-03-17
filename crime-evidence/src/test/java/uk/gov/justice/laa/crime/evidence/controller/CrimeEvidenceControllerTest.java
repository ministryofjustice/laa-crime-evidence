package uk.gov.justice.laa.crime.evidence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.evidence.CrimeEvidenceApplication;
import uk.gov.justice.laa.crime.evidence.config.CrimeEvidenceTestConfiguration;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.service.EvidenceService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(CrimeEvidenceTestConfiguration.class)
@SpringBootTest(classes = {CrimeEvidenceApplication.class}, webEnvironment = DEFINED_PORT)
@DirtiesContext
class CrimeEvidenceControllerTest {

    private static final boolean IS_VALID = true;
    private static final String CLIENT_SECRET = "secret";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String CLIENT_ID = "test-client";
    private static final String SCOPE_READ_WRITE = "READ_WRITE";
    private static final String ENDPOINT_URL = "/api/internal/v1/evidence/calculate-evidence-fee";

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EvidenceService evidenceService;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    private MockHttpServletRequestBuilder buildRequestGivenContent(HttpMethod method, String content) throws Exception {
        return buildRequestGivenContent(method, content, true);
    }

    private MockHttpServletRequestBuilder buildRequestGivenContent(HttpMethod method, String content, boolean withAuth) throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.request(method, ENDPOINT_URL)
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
    void calculateEvidenceFee_ServerError_RequestBodyIsMissing() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, ""))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void calculateEvidenceFee_BadRequest_RequestEmptyBody() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateEvidenceFee_Unauthorized_NoAccessToken() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", false))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void calculateEvidenceFee_RequestObjectFailsValidation() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, objectMapper.writeValueAsString(TestModelDataBuilder.getApiCalculateEvidenceFeeInvalidRequest())))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void calculateEvidenceFee_Success() throws Exception {
        var apiCalculateEvidenceFeeRequest =
                TestModelDataBuilder.getApiCalculateEvidenceFeeRequest(IS_VALID);
        var calculateEvidenceFeeRequestJson = objectMapper.writeValueAsString(apiCalculateEvidenceFeeRequest);
        var calculateEvidenceFeeResponse =
                TestModelDataBuilder.getApiCalculateEvidenceFeeResponse();

        when(evidenceService.calculateEvidenceFee(any(CrimeEvidenceDTO.class)))
                .thenReturn(calculateEvidenceFeeResponse);

        MvcResult result = mvc.perform(buildRequestGivenContent(HttpMethod.POST, calculateEvidenceFeeRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo(objectMapper.writeValueAsString(calculateEvidenceFeeResponse));
    }
}