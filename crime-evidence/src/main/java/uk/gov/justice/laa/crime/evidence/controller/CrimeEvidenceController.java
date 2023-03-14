package uk.gov.justice.laa.crime.evidence.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.laa.crime.evidence.dto.ErrorDTO;
import uk.gov.justice.laa.crime.evidence.model.common.ApiCalculateEvidenceFeeRequest;
import uk.gov.justice.laa.crime.evidence.model.common.ApiCalculateEvidenceFeeResponse;
import uk.gov.justice.laa.crime.evidence.service.EvidenceService;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/evidence")
@Tag(name = "Crown Evidence", description = "Rest API for Crown Evidence.")
public class CrimeEvidenceController {

    private final EvidenceService evidenceService;


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Process Rep Order Data")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiCalculateEvidenceFeeRequest.class)
            )
    )
    @ApiResponse(responseCode = "400",
            description = "Bad Request.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    @ApiResponse(responseCode = "500",
            description = "Server Error.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ResponseEntity<ApiCalculateEvidenceFeeResponse> calculateEvidenceFee(
            @Parameter(description = "Process Crown Rep Order",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiCalculateEvidenceFeeRequest.class)
                    )
            ) @Valid @RequestBody ApiCalculateEvidenceFeeRequest request) {

        //CrownCourtDTO requestDTO = preProcessRequest(request);
        //return ResponseEntity.ok(evidenceService.getRepOrderCapitalByRepId(requestDTO)
        return null;
        )
    }


}
