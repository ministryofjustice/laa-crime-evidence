package uk.gov.justice.laa.crime.evidence.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateUtil {

    private DateUtil() {}
    public static LocalDate parse(final String date) {
        return date != null ? LocalDate.parse(date) : null;
    }

    public static LocalDate parseLocalDate(final LocalDateTime date) {
        return date != null ? date.toLocalDate() : null;
    }

    public static String getLocalDateString(final LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : null;
    }

    public static LocalDateTime convertDateToDateTime(LocalDate date) {
        if (date != null) {
            return date.atTime(0, 0);
        } else return null;
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
