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
        assertThrows(IllegalArgumentException.class, () -> {
            incomeEvidenceValidationService.checkEvidenceReceivedDate(DateUtil.asDate(futureDate), new Date());
        });
    }

    @Test
    void givenValidDates_whenCheckEvidenceReceivedDateIsInvokedWithIncomeEvidenceReceivedDateBeforeCurrentDate_ExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        assertThrows(IllegalArgumentException.class, () -> {
            incomeEvidenceValidationService.checkEvidenceReceivedDate(DateUtil.asDate(pastDate), new Date());
        });
    }

    @Test
    void givenIncomeExtraEvidenceReceivedDate_whenCheckIncomeEvidenceReceivedDateIsInvoked_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkExtraEvidenceDescription("OTHER", "Some text");
    }

    @Test
    void givenIncomeExtraEvidenceReceivedDate_whenCheckIncomeEvidenceReceivedDateIsInvokedWithIncomeExtraEvidenceTextNull_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            incomeEvidenceValidationService.checkExtraEvidenceDescription("OTHER", null);
        });
    }

    @Test
    void givenIncomeExtraEvidenceReceivedDate_whenCheckIncomeEvidenceReceivedDateIsInvokedWithIncomeExtraEvidenceTextEmpty_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            incomeEvidenceValidationService.checkExtraEvidenceDescription("OTHER", "");
        });
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvoked_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceDueDates(new Date(), new Date(), new Date(), new Date());
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateNull_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            incomeEvidenceValidationService.checkEvidenceDueDates(null, new Date(), new Date(), new Date());
        });
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateAfterCurrentDate_thenNoExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        incomeEvidenceValidationService.checkEvidenceDueDates(DateUtil.asDate(futureDate), new Date(), new Date(), new Date());
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateBeforeCurrentDate_thenExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        assertThrows(IllegalArgumentException.class, () -> {
            incomeEvidenceValidationService.checkEvidenceDueDates(DateUtil.asDate(pastDate), new Date(), new Date(), new Date());
        });
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithFirstReminderDateNull_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceDueDates(new Date(), null, new Date(), new Date());
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithFirstReminderDateAfterCurrentDate_thenNoExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        incomeEvidenceValidationService.checkEvidenceDueDates(new Date(), DateUtil.asDate(futureDate), new Date(), new Date());
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithFirstReminderDateBeforeCurrentDate_thenNoExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        incomeEvidenceValidationService.checkEvidenceDueDates(new Date(), DateUtil.asDate(pastDate), new Date(), new Date());
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithSecondReminderDateNull_thenExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceDueDates(new Date(), new Date(), null, new Date());
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithExistingEvidenceDueDateAsNullAndEvidenceDueDateBeforeDate_thenExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        assertThrows(IllegalArgumentException.class, () -> {
            incomeEvidenceValidationService.checkEvidenceDueDates(DateUtil.asDate(pastDate), new Date(), new Date(), null);
        });
    }
}