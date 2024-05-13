package uk.gov.justice.laa.crime.evidence.common;

public class Constants {

    public static final String LAA_TRANSACTION_ID = "Laa-Transaction-Id";
    public static final String SENT_FOR_TRIAL = "SENT FOR TRIAL";
    public static final String COMMITTED_FOR_TRIAL = "COMMITTED FOR TRIAL";

    public static final Integer FIRST_REMINDER_DAYS_DUE = 24;

    public static final Integer SECOND_REMINDER_DAYS_DUE = 7;

    private Constants() {
        throw new IllegalStateException("Constants class");
    }

}
