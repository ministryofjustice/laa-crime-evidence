package uk.gov.justice.laa.crime.evidence.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.evidence.dto.ErrorDTO;


@RestControllerAdvice
@Slf4j
public class CrimeEvidenceExceptionHandler {

    private static ResponseEntity<ErrorDTO> buildErrorResponse(HttpStatus status, String errorMessage) {
        return new ResponseEntity<>(ErrorDTO.builder().code(status.toString()).message(errorMessage).build(), status);
    }

    @ExceptionHandler(APIClientException.class)
    public ResponseEntity<ErrorDTO> handleApiClientError(APIClientException ex) {
        log.error("APIClientException: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(CrimeEvidenceDataException.class)
    public ResponseEntity<ErrorDTO> handleCrimeEvidenceDataException(CrimeEvidenceDataException ex) {
        log.error("CrimeEvidenceDataException: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

}
