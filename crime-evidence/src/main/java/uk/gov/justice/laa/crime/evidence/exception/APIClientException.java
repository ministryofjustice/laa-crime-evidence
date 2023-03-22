package uk.gov.justice.laa.crime.evidence.exception;

public class APIClientException extends RuntimeException {

    public APIClientException(String message) {
        super(message);
    }

    public APIClientException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
