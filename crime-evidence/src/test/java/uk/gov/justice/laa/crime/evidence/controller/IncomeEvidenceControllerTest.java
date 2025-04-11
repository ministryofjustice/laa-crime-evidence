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
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.service.IncomeEvidenceService;
import uk.gov.justice.laa.crime.evidence.tracing.TraceIdHandler;
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

    @MockBean
    private IncomeEvidenceService incomeEvidenceService;

    @Test
    void givenMissingRequestBody_whenCreateEvidenceIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenEmptyRequestBody_whenCreateEvidenceIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidRequest_whenCreateEvidenceIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        ApiCreateIncomeEvidenceRequest request = TestModelDataBuilder.getApiCreateIncomeEvidenceRequest();
        request.setMagCourtOutcome(null);
        String content = objectMapper.writeValueAsString(request);
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, content, ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidRequest_whenCreateEvidenceIsInvoked_thenOkResponseIsReturned() throws Exception {
        ApiCreateIncomeEvidenceRequest request = TestModelDataBuilder.getApiCreateIncomeEvidenceRequest();
        String content = objectMapper.writeValueAsString(request);
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.POST, content, ENDPOINT_URL))
                .andExpect(status().isOk());
    }

    @Test
    void givenMissingRequestBody_whenUpdateEvidenceIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.PUT, "", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenEmptyRequestBody_whenUpdateEvidenceIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidRequest_whenUpdateEvidenceIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        ApiUpdateIncomeEvidenceRequest request = TestModelDataBuilder.getApiUpdateIncomeEvidenceRequest();
        request.setApplicantEvidenceItems(TestModelDataBuilder.getApiIncomeEvidenceItems());
        request.setMagCourtOutcome(null);
        String content = objectMapper.writeValueAsString(request);
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.PUT, content, ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidRequestWithApplicantDetails_whenUpdateEvidenceIsInvoked_thenOkResponseIsReturned() throws Exception {
        ApiUpdateIncomeEvidenceRequest request = TestModelDataBuilder.getApiUpdateIncomeEvidenceRequest();
        request.setApplicantEvidenceItems(TestModelDataBuilder.getApiIncomeEvidenceItems());
        String content = objectMapper.writeValueAsString(request);
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.PUT, content, ENDPOINT_URL))
                .andExpect(status().isOk());
    }

    @Test
    void givenValidRequestWithPartnerDetails_whenUpdateEvidenceIsInvoked_thenOkResponseIsReturned() throws Exception {
        ApiUpdateIncomeEvidenceRequest request = TestModelDataBuilder.getApiUpdateIncomeEvidenceRequest();
        request.setPartnerEvidenceItems(TestModelDataBuilder.getApiIncomeEvidenceItems());
        String content = objectMapper.writeValueAsString(request);
        mvc.perform(RequestBuilderUtils.buildRequestGivenContent(HttpMethod.PUT, content, ENDPOINT_URL))
            .andExpect(status().isOk());
    }
}
