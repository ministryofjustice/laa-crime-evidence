package uk.gov.justice.laa.crime.evidence.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.justice.laa.crime.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;

public interface IncomeEvidenceApi {

    @Operation(description = "Create Income Evidence")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiCreateIncomeEvidenceResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    ResponseEntity<ApiCreateIncomeEvidenceResponse> createEvidence(
            @Parameter(description = "Create Income Evidence Records",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiCreateIncomeEvidenceRequest.class)
                    )
            ) @Valid @RequestBody ApiCreateIncomeEvidenceRequest request);


    @Operation(description = "Update Income Evidence")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiUpdateIncomeEvidenceResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    ResponseEntity<ApiUpdateIncomeEvidenceResponse> updateEvidence(
            @Parameter(description = "Update Income Evidence Records",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiUpdateIncomeEvidenceRequest.class)
                    )
            ) @Valid @RequestBody ApiUpdateIncomeEvidenceRequest request);
}
