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
import uk.gov.justice.laa.crime.evidence.builder.CreateEvidenceDTOBuilder;
import uk.gov.justice.laa.crime.evidence.builder.UpdateEvidenceDTOBuilder;
import uk.gov.justice.laa.crime.evidence.dto.CreateEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.service.EvidenceService;
import uk.gov.justice.laa.crime.evidence.service.IncomeEvidenceService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/evidence")
@Tag(name = "Income Evidence", description = "Rest API for Income Evidence")
public class IncomeEvidenceController implements IncomeEvidenceApi {

    private final EvidenceService evidenceService;
    private final IncomeEvidenceService incomeEvidenceService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiCreateIncomeEvidenceResponse> createEvidence(ApiCreateIncomeEvidenceRequest request) {
        CreateEvidenceDTO createEvidenceDTO = CreateEvidenceDTOBuilder.build(request);
        return ResponseEntity.ok(incomeEvidenceService.createEvidence(createEvidenceDTO));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiUpdateIncomeEvidenceResponse> updateEvidence(ApiUpdateIncomeEvidenceRequest request) {
        UpdateEvidenceDTO updateEvidenceDTO = UpdateEvidenceDTOBuilder.build(request);

        return ResponseEntity.ok(evidenceService.updateEvidence(updateEvidenceDTO));
    }
}
