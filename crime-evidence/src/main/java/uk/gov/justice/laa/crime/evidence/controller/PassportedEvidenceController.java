package uk.gov.justice.laa.crime.evidence.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.evidence.service.PassportedEvidenceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/evidence/passported")
@Tag(name = "Passported Evidence", description = "Rest API for Passported Evidence")
public class PassportedEvidenceController implements PassportedEvidenceApi {

    private final PassportedEvidenceService passportedEvidenceService;

    @GetMapping(path = "/{passportedAssessmentId}")
    public ResponseEntity<ApiGetPassportedAssessmentResponse> find(@PathVariable int passportedAssessmentId) {
        return ResponseEntity.ok(passportedEvidenceService.getPassportedEvidence(passportedAssessmentId));
    }
}
