package uk.gov.justice.laa.crime.evidence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.service.EvidenceService;
import uk.gov.justice.laa.crime.evidence.util.RequestBuilderUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@Import(CrimeEvidenceTestConfiguration.class)
@SpringBootTest(classes = {CrimeEvidenceApplication.class}, webEnvironment = DEFINED_PORT)
class CrimeEvidenceControllerTest {

    private static final boolean IS_VALID = true;
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

    @Test
    void calculateEvidenceFee_ServerError_RequestBodyIsMissing() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "", ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void calculateEvidenceFee_BadRequest_RequestEmptyBody() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateEvidenceFee_Unauthorized_NoAccessToken() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL, false))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void calculateEvidenceFee_RequestObjectFailsValidation() throws Exception {
        String content = objectMapper.writeValueAsString(TestModelDataBuilder.getApiCalculateEvidenceFeeInvalidRequest());
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, content, ENDPOINT_URL))
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

        MvcResult result = mvc.perform(RequestBuilderUtils.buildRequestGivenContent(
                        HttpMethod.POST, calculateEvidenceFeeRequestJson, ENDPOINT_URL)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String expected = objectMapper.writeValueAsString(calculateEvidenceFeeResponse);
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expected);
    }
}