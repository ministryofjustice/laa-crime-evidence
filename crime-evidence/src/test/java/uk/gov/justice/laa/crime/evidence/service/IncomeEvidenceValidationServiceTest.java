package uk.gov.justice.laa.crime.evidence.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceDTO;
import uk.gov.justice.laa.crime.util.DateUtil;

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
        incomeEvidenceValidationService.checkEvidenceReceivedDate(LocalDate.now(), LocalDate.now());
    }

    @Test
    void givenValidDates_whenCheckEvidenceReceivedDateIsInvokedWithIncomeEvidenceReceivedDateNull_noExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceReceivedDate(null, LocalDate.now());
    }

    @Test
    void givenValidDates_whenCheckEvidenceReceivedDateIsInvokedWithIncomeEvidenceReceivedDateAfterCurrentDate_thenExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        LocalDate currentDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                incomeEvidenceValidationService.checkEvidenceReceivedDate(futureDate, currentDate)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceReceivedDateIsInvokedWithIncomeEvidenceReceivedDateBeforeCurrentDate_ExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        LocalDate currentDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                incomeEvidenceValidationService.checkEvidenceReceivedDate(pastDate, currentDate)
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
        incomeEvidenceValidationService.checkEvidenceDueDates(LocalDate.now(), LocalDate.now(), LocalDate.now(), LocalDate.now());
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateNull_thenExceptionIsThrown() {
        LocalDate currentDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                incomeEvidenceValidationService.checkEvidenceDueDates(null, currentDate, currentDate, currentDate)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateAfterCurrentDate_thenNoExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        LocalDate currentDate = LocalDate.now();
        incomeEvidenceValidationService.checkEvidenceDueDates(futureDate, currentDate, currentDate, currentDate);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateBeforeCurrentDate_thenExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        LocalDate currentDate1 = LocalDate.now();
        LocalDate currentDate2 = LocalDate.now();
        LocalDate currentDate3 = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                incomeEvidenceValidationService.checkEvidenceDueDates(pastDate, currentDate1, currentDate2, currentDate3)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithFirstReminderDateNull_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceDueDates(LocalDate.now(), null, LocalDate.now(), LocalDate.now());
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithFirstReminderDateAfterCurrentDate_thenNoExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        LocalDate currentDate1 = LocalDate.now();
        LocalDate currentDate2 = LocalDate.now();
        LocalDate currentDate3 = LocalDate.now();
        incomeEvidenceValidationService.checkEvidenceDueDates(currentDate1, futureDate, currentDate2, currentDate3);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithFirstReminderDateBeforeCurrentDate_thenNoExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        LocalDate currentDate1 = LocalDate.now();
        LocalDate currentDate2 = LocalDate.now();
        LocalDate currentDate3 = LocalDate.now();
        incomeEvidenceValidationService.checkEvidenceDueDates(currentDate1, pastDate, currentDate2, currentDate3);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithSecondReminderDateNull_thenExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceDueDates(LocalDate.now(), LocalDate.now(), null, LocalDate.now());
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithExistingEvidenceDueDateAsNullAndEvidenceDueDateBeforeDate_thenExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        LocalDate currentDate1 = LocalDate.now();
        LocalDate currentDate2 = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                incomeEvidenceValidationService.checkEvidenceDueDates(pastDate, currentDate1, currentDate2, null)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithExistingEvidenceDueDateAsNullAndEvidenceDueDateAfterDate_thenNoExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        LocalDate currentDate1 = LocalDate.now();
        LocalDate currentDate2 = LocalDate.now();
        incomeEvidenceValidationService.checkEvidenceDueDates(futureDate, currentDate1, currentDate2, null);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithExistingEvidenceDueDateAsNullAndEvidenceDueDateAsNull_thenNoExceptionIsThrown() {
        LocalDate currentDate1 = LocalDate.now();
        LocalDate currentDate2 = LocalDate.now();
        incomeEvidenceValidationService.checkEvidenceDueDates(null, currentDate1, currentDate2, null);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithSecondReminderDateAfterCurrentDate_thenNoExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        LocalDate currentDate1 = LocalDate.now();
        LocalDate currentDate2 = LocalDate.now();
        incomeEvidenceValidationService.checkEvidenceDueDates(currentDate1, currentDate2, futureDate, currentDate2);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithSecondReminderDateBeforeCurrentDate_thenNoExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        LocalDate currentDate1 = LocalDate.now();
        LocalDate currentDate2 = LocalDate.now();
        incomeEvidenceValidationService.checkEvidenceDueDates(currentDate1, currentDate2, pastDate, currentDate2);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithExistingEvidenceDueDateBeforeCurrentDate_thenNoExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        LocalDate currentDate1 = LocalDate.now();
        LocalDate currentDate2 = LocalDate.now();
        incomeEvidenceValidationService.checkEvidenceDueDates(currentDate1, currentDate2, currentDate2, pastDate);
    }

    @Test
    void givenValidDates_whenValidateIsInvoked_thenNoExceptionIsThrown() {
        EvidenceDTO evidenceDTO = EvidenceDTO.builder().evidenceDueDate(DateUtil.convertDateToDateTime(LocalDate.now().plusMonths(2)))
                .firstReminderDate(DateUtil.convertDateToDateTime(LocalDate.now().plusMonths(1)))
                .secondReminderDate(DateUtil.convertDateToDateTime(LocalDate.now().plusMonths(1)))
                .existingEvidenceDueDate(DateUtil.convertDateToDateTime(LocalDate.now().minusMonths(1)))
                .incomeExtraEvidence("OTHER")
                .incomeExtraEvidenceText("Some text")
                .incomeEvidenceReceivedDate(DateUtil.convertDateToDateTime(LocalDate.now().minusMonths(1)))
                .applicationReceivedDate(DateUtil.convertDateToDateTime(LocalDate.now().minusMonths(2)))
                .build();
        incomeEvidenceValidationService.validate(evidenceDTO);
    }
}