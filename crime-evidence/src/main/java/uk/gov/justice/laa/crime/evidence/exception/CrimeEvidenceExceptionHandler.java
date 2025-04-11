package uk.gov.justice.laa.crime.evidence.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.evidence.dto.ErrorDTO;
import uk.gov.justice.laa.crime.evidence.tracing.TraceIdHandler;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CrimeEvidenceExceptionHandler {

    private final TraceIdHandler traceIdHandler;
    private final ObjectMapper mapper;

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorDTO> handleRuntimeException(WebClientResponseException exception) {
        String errorMessage;

        try {
            ErrorDTO errorDTO = mapper.readValue(exception.getResponseBodyAsString(), ErrorDTO.class);
            errorMessage = errorDTO.getMessage();
        } catch (IOException ex) {
            log.warn("Unable to read the ErrorDTO from WebClientResponseException", ex);
            errorMessage = exception.getMessage();
        }

        return buildErrorResponse(exception.getStatusCode(), errorMessage, traceIdHandler.getTraceId());
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ErrorDTO> handleRuntimeException(WebClientRequestException exception) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), traceIdHandler.getTraceId());
    }

    @ExceptionHandler(CrimeEvidenceDataException.class)
    public ResponseEntity<ErrorDTO> handleCrimeEvidenceDataException(CrimeEvidenceDataException ex) {
        log.error("CrimeEvidenceDataException: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), traceIdHandler.getTraceId());
    }

    private static ResponseEntity<ErrorDTO> buildErrorResponse(HttpStatusCode status, String errorMessage, String traceId) {
        log.error("Exception Occurred. Status - {}, Detail - {}, TraceId - {}", status, errorMessage, traceId);
        return new ResponseEntity<>(ErrorDTO.builder().traceId(traceId).code(status.toString()).message(errorMessage).build(), status);
    }
}
