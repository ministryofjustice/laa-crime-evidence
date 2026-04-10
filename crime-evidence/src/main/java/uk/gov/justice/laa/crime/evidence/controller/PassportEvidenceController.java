package uk.gov.justice.laa.crime.evidence.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.evidence.service.PassportEvidenceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/evidence/passport")
@Tag(name = "Passport Evidence", description = "Rest API for Passport Evidence")
public class PassportEvidenceController implements PassportEvidenceApi {

    private final PassportEvidenceService passportEvidenceService;

    @GetMapping(path = "/{passportAssessmentId}")
    public ResponseEntity<ApiGetPassportEvidenceResponse> find(@PathVariable int passportAssessmentId) {
        return ResponseEntity.ok(passportEvidenceService.getPassportEvidence(passportAssessmentId));
    }
}
