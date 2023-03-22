package uk.gov.justice.laa.crime.evidence.exception;

public class RetryableWebClientResponseException extends RuntimeException {

    public RetryableWebClientResponseException() {
        super();
    }

    public RetryableWebClientResponseException(String message) {
        super(message);
    }
}