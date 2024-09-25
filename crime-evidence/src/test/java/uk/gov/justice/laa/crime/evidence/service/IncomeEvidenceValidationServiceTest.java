package uk.gov.justice.laa.crime.evidence.service;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class IncomeEvidenceValidationServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private IncomeEvidenceValidationService incomeEvidenceValidationService;

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenValidDates_whenCheckEvidenceReceivedDateIsInvoked_noExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceReceivedDate(new Date(), new Date());
    }

    @Test
    void givenValidDates_whenCheckEvidenceReceivedDateIsInvokedWithIncomeEvidenceReceivedDateNull_noExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceReceivedDate(null, new Date());
    }

    @Test
    void givenValidDates_whenCheckEvidenceReceivedDateIsInvokedWithIncomeEvidenceReceivedDateAfterCurrentDate_thenExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        Date convertedFutureDate = DateUtil.asDate(futureDate);
        Date currentDate = new Date();
        assertThrows(IllegalArgumentException.class, () ->
                incomeEvidenceValidationService.checkEvidenceReceivedDate(convertedFutureDate, currentDate)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceReceivedDateIsInvokedWithIncomeEvidenceReceivedDateBeforeCurrentDate_ExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        Date pastDateConverted = DateUtil.asDate(pastDate);
        Date currentDate = new Date();
        assertThrows(IllegalArgumentException.class, () ->
                incomeEvidenceValidationService.checkEvidenceReceivedDate(pastDateConverted, currentDate)
        );
    }

    @Test
    void givenIncomeExtraEvidenceReceivedDate_whenCheckExtraEvidenceDescriptionIsInvoked_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkExtraEvidenceDescription("OTHER", "Some text");
    }

    @Test
    void givenIncomeExtraEvidenceReceivedDate_whenCheckExtraEvidenceDescriptionIsInvokedWithIncomeExtraEvidenceTextNull_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            incomeEvidenceValidationService.checkExtraEvidenceDescription("OTHER", null);
        });
    }

    @Test
    void givenIncomeExtraEvidenceReceivedDate_whenCheckExtraEvidenceDescriptionIsInvokedWithIncomeExtraEvidenceTextEmpty_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            incomeEvidenceValidationService.checkExtraEvidenceDescription("OTHER", "");
        });
    }

    @Test
    void givenIncomeExtraEvidenceReceivedDate_whenCheckExtraEvidenceDescriptionIsInvokedWithIncomeExtraEvidenceTextBlank_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            incomeEvidenceValidationService.checkExtraEvidenceDescription("OTHER", " ");
        });
    }

    @Test
    void givenIncomeExtraEvidenceReceivedDate_whenCheckExtraEvidenceDescriptionIsInvokedWithIncomeExtraEvidenceNull_thenNoExceptionIsThrown() {
            incomeEvidenceValidationService.checkExtraEvidenceDescription(null, "Some text");
    }

    @Test
    void givenIncomeExtraEvidenceReceivedDate_whenCheckExtraEvidenceDescriptionIsInvokedWithIncomeExtraEvidenceEmpty_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkExtraEvidenceDescription("", "Some text");
    }

    @Test
    void givenIncomeExtraEvidenceReceivedDate_whenCheckExtraEvidenceDescriptionIsInvokedWithIncomeExtraEvidenceBlank_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkExtraEvidenceDescription(" ", "Some text");
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvoked_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceDueDates(new Date(), new Date(), new Date(), new Date());
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateNull_thenExceptionIsThrown() {
        Date currentDate = new Date();
        assertThrows(IllegalArgumentException.class, () ->
                incomeEvidenceValidationService.checkEvidenceDueDates(null, currentDate, currentDate, currentDate)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateAfterCurrentDate_thenNoExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        Date futureDateConverted = DateUtil.asDate(futureDate);
        Date currentDate = new Date();
        incomeEvidenceValidationService.checkEvidenceDueDates(futureDateConverted, currentDate, currentDate, currentDate);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateBeforeCurrentDate_thenExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        Date pastDateConverted = DateUtil.asDate(pastDate);
        Date currentDate1 = new Date();
        Date currentDate2 = new Date();
        Date currentDate3 = new Date();
        assertThrows(IllegalArgumentException.class, () ->
                incomeEvidenceValidationService.checkEvidenceDueDates(pastDateConverted, currentDate1, currentDate2, currentDate3)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithFirstReminderDateNull_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceDueDates(new Date(), null, new Date(), new Date());
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithFirstReminderDateAfterCurrentDate_thenNoExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        Date currentDate1 = new Date();
        Date futureDateConverted = DateUtil.asDate(futureDate);
        Date currentDate2 = new Date();
        Date currentDate3 = new Date();
        incomeEvidenceValidationService.checkEvidenceDueDates(currentDate1, futureDateConverted, currentDate2, currentDate3);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithFirstReminderDateBeforeCurrentDate_thenNoExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        Date currentDate1 = new Date();
        Date pastDateConverted = DateUtil.asDate(pastDate);
        Date currentDate2 = new Date();
        Date currentDate3 = new Date();
        incomeEvidenceValidationService.checkEvidenceDueDates(currentDate1, pastDateConverted, currentDate2, currentDate3);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithSecondReminderDateNull_thenExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceDueDates(new Date(), new Date(), null, new Date());
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithExistingEvidenceDueDateAsNullAndEvidenceDueDateBeforeDate_thenExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        Date pastDateConverted = DateUtil.asDate(pastDate);
        Date currentDate1 = new Date();
        Date currentDate2 = new Date();
        assertThrows(IllegalArgumentException.class, () ->
                incomeEvidenceValidationService.checkEvidenceDueDates(pastDateConverted, currentDate1, currentDate2, null)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithExistingEvidenceDueDateAsNullAndEvidenceDueDateAfterDate_thenNoExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        Date futureDateConverted = DateUtil.asDate(futureDate);
        Date currentDate1 = new Date();
        Date currentDate2 = new Date();
        incomeEvidenceValidationService.checkEvidenceDueDates(futureDateConverted, currentDate1, currentDate2, null);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithExistingEvidenceDueDateAsNullAndEvidenceDueDateAsNull_thenNoExceptionIsThrown() {
        Date currentDate1 = new Date();
        Date currentDate2 = new Date();
        incomeEvidenceValidationService.checkEvidenceDueDates(null, currentDate1, currentDate2, null);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithSecondReminderDateAfterCurrentDate_thenNoExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        Date currentDate1 = new Date();
        Date currentDate2 = new Date();
        Date futureDateConverted = DateUtil.asDate(futureDate);
        incomeEvidenceValidationService.checkEvidenceDueDates(currentDate1, currentDate2, futureDateConverted, currentDate2);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithSecondReminderDateBeforeCurrentDate_thenNoExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        Date currentDate1 = new Date();
        Date currentDate2 = new Date();
        Date pastDateConverted = DateUtil.asDate(pastDate);
        incomeEvidenceValidationService.checkEvidenceDueDates(currentDate1, currentDate2, pastDateConverted, currentDate2);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithExistingEvidenceDueDateBeforeCurrentDate_thenNoExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        Date currentDate1 = new Date();
        Date currentDate2 = new Date();
        Date pastDateConverted = DateUtil.asDate(pastDate);
        incomeEvidenceValidationService.checkEvidenceDueDates(currentDate1, currentDate2, currentDate2, pastDateConverted);
    }
}