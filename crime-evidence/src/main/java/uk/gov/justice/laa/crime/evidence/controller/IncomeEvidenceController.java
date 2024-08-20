package uk.gov.justice.laa.crime.evidence.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/evidence")
@Tag(name = "Income Evidence", description = "Rest API for Income Evidence")
public class IncomeEvidenceController implements IncomeEvidenceApi {

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiCreateIncomeEvidenceResponse> createEvidence(ApiCreateIncomeEvidenceRequest request) {
        return ResponseEntity.ok().build();
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiUpdateIncomeEvidenceResponse> updateEvidence(ApiUpdateIncomeEvidenceRequest request) {
        return ResponseEntity.ok().build();
    }
}
