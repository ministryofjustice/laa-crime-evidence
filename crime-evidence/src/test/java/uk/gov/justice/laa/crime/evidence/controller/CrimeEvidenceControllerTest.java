package uk.gov.justice.laa.crime.evidence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.justice.laa.crime.commons.tracing.TraceIdHandler;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.service.EvidenceService;
import uk.gov.justice.laa.crime.util.RequestBuilderUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CrimeEvidenceController.class)
@AutoConfigureMockMvc(addFilters = false)
class CrimeEvidenceControllerTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/evidence/calculate-evidence-fee";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EvidenceService evidenceService;

    @MockBean
    private TraceIdHandler traceIdHandler;

    @Test
    void givenMissingRequestBody_whenCalculateEvidenceFeeIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "", ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void givenEmptyRequestBody_whenCalculateEvidenceFeeIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidRequest_whenCalculateEvidenceFeeIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        String content = objectMapper.writeValueAsString(TestModelDataBuilder.getApiCalculateEvidenceFeeInvalidRequest());
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, content, ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void givenValidRequest_whenCalculateEvidenceFeeIsInvoked_thenOkResponseIsReturned() throws Exception {
        var apiCalculateEvidenceFeeRequest =
                TestModelDataBuilder.getApiCalculateEvidenceFeeRequest(true);
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