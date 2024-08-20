package uk.gov.justice.laa.crime.evidence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.commons.tracing.TraceIdHandler;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.util.RequestBuilderUtils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IncomeEvidenceController.class)
@AutoConfigureMockMvc(addFilters = false)
class IncomeEvidenceControllerTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/evidence";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TraceIdHandler traceIdHandler;

    @Test
    void createEvidence_ServerError_RequestBodyIsMissing() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "", ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createEvidence_BadRequest_RequestEmptyBody() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvidence_Unauthorized_NoAccessToken() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL, false))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createEvidence_RequestObjectFailsValidation() throws Exception {
        ApiCreateIncomeEvidenceRequest request = TestModelDataBuilder.getApiCreateIncomeEvidenceRequest();
        request.setMagCourtOutcome(null);
        String content = objectMapper.writeValueAsString(request);
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, content, ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createEvidence_Success() throws Exception {
        ApiCreateIncomeEvidenceRequest request = TestModelDataBuilder.getApiCreateIncomeEvidenceRequest();
        String content = objectMapper.writeValueAsString(request);
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, content, ENDPOINT_URL))
                .andExpect(status().isOk());
    }

    @Test
    void updateEvidence_ServerError_RequestBodyIsMissing() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.PUT, "", ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateEvidence_BadRequest_RequestEmptyBody() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEvidence_Unauthorized_NoAccessToken() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL, false))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateEvidence_RequestObjectFailsValidation() throws Exception {
        ApiUpdateIncomeEvidenceRequest request = TestModelDataBuilder.getApiUpdateIncomeEvidenceRequest();
        request.setMagCourtOutcome(null);
        String content = objectMapper.writeValueAsString(request);
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.PUT, content, ENDPOINT_URL))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateEvidence_Success() throws Exception {
        ApiUpdateIncomeEvidenceRequest request = TestModelDataBuilder.getApiUpdateIncomeEvidenceRequest();
        String content = objectMapper.writeValueAsString(request);
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.PUT, content, ENDPOINT_URL))
                .andExpect(status().isOk());
    }
}
