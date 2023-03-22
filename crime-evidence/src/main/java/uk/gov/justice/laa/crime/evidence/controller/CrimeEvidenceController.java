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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.evidence.builder.CrimeEvidenceDTOBuilder;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.ErrorDTO;
import uk.gov.justice.laa.crime.evidence.model.common.ApiCalculateEvidenceFeeRequest;
import uk.gov.justice.laa.crime.evidence.model.common.ApiCalculateEvidenceFeeResponse;
import uk.gov.justice.laa.crime.evidence.service.EvidenceService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/evidence")
@Tag(name = "Crime Evidence", description = "Rest API for Crime Evidence.")
public class CrimeEvidenceController {

    private final EvidenceService evidenceService;


    @PostMapping(value = "/calculate-evidence-fee", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Calculate Crime Evidence Fee")
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
            @Parameter(description = "Calculate Crime Evidence Fee",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiCalculateEvidenceFeeRequest.class)
                    )
            ) @Valid @RequestBody ApiCalculateEvidenceFeeRequest request) {
        CrimeEvidenceDTO requestDTO = preProcessRequest(request);
        return ResponseEntity.ok(evidenceService.calculateEvidenceFee(requestDTO));
    }


    private CrimeEvidenceDTO preProcessRequest(ApiCalculateEvidenceFeeRequest request) {
        return CrimeEvidenceDTOBuilder.build(request);
    }

}
