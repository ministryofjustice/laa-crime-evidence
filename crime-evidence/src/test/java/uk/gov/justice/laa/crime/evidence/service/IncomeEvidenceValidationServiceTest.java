package uk.gov.justice.laa.crime.evidence.service;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.justice.laa.crime.evidence.service.IncomeEvidenceValidationService.MISSING_OTHER_EVIDENCE_DESCRIPTION;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class IncomeEvidenceValidationServiceTest {

    @InjectMocks
    private IncomeEvidenceValidationService incomeEvidenceValidationService;

    @Test
    void givenValidDates_whenCheckEvidenceReceivedDateIsInvoked_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceReceivedDate(LocalDate.now(), LocalDate.now());
    }

    @Test
    void givenValidDates_whenCheckEvidenceReceivedDateIsInvokedWithIncomeEvidenceReceivedDateNull_thenNoExceptionIsThrown() {
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
    void givenValidDates_whenCheckEvidenceReceivedDateIsInvokedWithIncomeEvidenceReceivedDateBeforeCurrentDate_thenExceptionIsThrown() {
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
    void givenValidDatesAndNoEvidencePending_whenCheckEvidenceDueDatesIsInvoked_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceDueDates(LocalDate.now(), LocalDate.now(), false);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateNullAndEvidencePending_thenExceptionIsThrown() {
        LocalDate currentDate = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                incomeEvidenceValidationService.checkEvidenceDueDates(null, currentDate, true)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateAfterCurrentDate_thenNoExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        LocalDate currentDate = LocalDate.now();
        incomeEvidenceValidationService.checkEvidenceDueDates(futureDate, currentDate, false);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateBeforeCurrentDate_thenExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        LocalDate currentDate1 = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () ->
                incomeEvidenceValidationService.checkEvidenceDueDates(pastDate, currentDate1, false)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedToAttemptToSetDueDateInThePast_thenExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        assertThrows(IllegalArgumentException.class, () ->
            incomeEvidenceValidationService.checkEvidenceDueDates(pastDate, null, false)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithNoEvidencePending_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceDueDates(LocalDate.now(), null, false);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidencePending_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceDueDates(LocalDate.now(), LocalDate.now(), true);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithExistingEvidenceDueDateAsNullAndEvidenceDueDateAfterDate_thenNoExceptionIsThrown() {
        LocalDate futureDate = LocalDate.now().plusMonths(2);
        LocalDate currentDate1 = LocalDate.now();
        incomeEvidenceValidationService.checkEvidenceDueDates(futureDate, currentDate1, false);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithExistingEvidenceDueDateAsNullAndEvidenceDueDateAsNull_thenNoExceptionIsThrown() {
        LocalDate currentDate1 = LocalDate.now();
        incomeEvidenceValidationService.checkEvidenceDueDates(null, currentDate1, false);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithExistingEvidenceDueDateBeforeCurrentDate_thenNoExceptionIsThrown() {
        LocalDate currentDate1 = LocalDate.now();
        LocalDate currentDate2 = LocalDate.now();
        incomeEvidenceValidationService.checkEvidenceDueDates(currentDate1, currentDate2, false);
    }

    @Test
    void givenExtraEvidenceWithDescription_whenCheckExtraEvidenceDescriptionsIsInvoked_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkExtraEvidenceDescriptions(
                List.of(TestModelDataBuilder.getIncomeEvidence(IncomeEvidenceType.OTHER)));
    }

    @Test
    void givenIncomeExtraEvidenceWithNoDescription_whenCheckExtraEvidenceDescriptionsIsInvoked_thenExceptionIsThrown() {
        ApiIncomeEvidence extraIncomeEvidence = TestModelDataBuilder.getIncomeEvidence(IncomeEvidenceType.OTHER_ADHOC);
        extraIncomeEvidence.setDescription(null);
        List<ApiIncomeEvidence> evidenceList = List.of(extraIncomeEvidence);
        assertThrows(IllegalArgumentException.class,
                () -> incomeEvidenceValidationService.checkExtraEvidenceDescriptions(evidenceList),
                MISSING_OTHER_EVIDENCE_DESCRIPTION);
    }

    @Test
    void givenIncomeExtraEvidenceWithEmptyDescription_whenCheckExtraEvidenceDescriptionsIsInvoked_thenExceptionIsThrown() {
        ApiIncomeEvidence extraIncomeEvidence = TestModelDataBuilder.getIncomeEvidence(IncomeEvidenceType.OTHER_BUSINESS);
        extraIncomeEvidence.setDescription("");
        List<ApiIncomeEvidence> evidenceList = List.of(extraIncomeEvidence);
        assertThrows(IllegalArgumentException.class,
                () -> incomeEvidenceValidationService.checkExtraEvidenceDescriptions(evidenceList),
                MISSING_OTHER_EVIDENCE_DESCRIPTION);
    }

    @Test
    void givenIncomeEvidence_whenCheckExtraEvidenceDescriptionsIsInvoked_thenNoExceptionIsThrown() {
        ApiIncomeEvidence incomeEvidence = TestModelDataBuilder.getIncomeEvidence(IncomeEvidenceType.NINO);
        incomeEvidence.setDescription("");
        incomeEvidenceValidationService.checkExtraEvidenceDescriptions(List.of(incomeEvidence));
    }
}