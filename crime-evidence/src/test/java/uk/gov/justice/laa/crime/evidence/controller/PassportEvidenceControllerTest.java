package uk.gov.justice.laa.crime.evidence.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.service.PassportEvidenceService;
import uk.gov.justice.laa.crime.evidence.tracing.TraceIdHandler;
import uk.gov.justice.laa.crime.util.RequestBuilderUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PassportEvidenceController.class)
@AutoConfigureMockMvc(addFilters = false)
class PassportEvidenceControllerTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/evidence/passport";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TraceIdHandler traceIdHandler;

    @MockitoBean
    private PassportEvidenceService passportEvidenceService;

    @Test
    void givenValidId_whenFindIsInvoked_thenPassportEvidenceResponseIsReturned() throws Exception {
        when(passportEvidenceService.getPassportEvidence(TestModelDataBuilder.PASSPORT_ASSESSMENT_ID))
                .thenReturn(TestModelDataBuilder.getApiPassportEvidenceResponse());

        mvc.perform(RequestBuilderUtils.buildRequest(
                        HttpMethod.GET, ENDPOINT_URL + "/" + TestModelDataBuilder.PASSPORT_ASSESSMENT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.applicantEvidenceItems[0].description").value("mock evidence item"));
    }

    @Test
    void givenInvalidId_whenFindIsInvoked_thenErrorResponseIsReturned() throws Exception {
        mvc.perform(RequestBuilderUtils.buildRequest(HttpMethod.GET, ENDPOINT_URL + "/1NV4L1D"))
                .andExpect(status().isBadRequest());
    }
}
