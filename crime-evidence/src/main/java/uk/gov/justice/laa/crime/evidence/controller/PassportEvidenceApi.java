package uk.gov.justice.laa.crime.evidence.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import uk.gov.justice.laa.crime.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface PassportEvidenceApi {
    @Operation(description = "Retrieve Passported Assessment Evidence")
    @ApiResponse(
            responseCode = "200",
            description = "Passport assessment evidence found",
            content =
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiGetPassportEvidenceResponse.class)))
    @DefaultHTTPErrorResponse
    ResponseEntity<ApiGetPassportEvidenceResponse> find(@PathVariable int passportedAssessmentId);
}
