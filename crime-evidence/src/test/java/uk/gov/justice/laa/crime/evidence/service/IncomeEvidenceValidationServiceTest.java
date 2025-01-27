package uk.gov.justice.laa.crime.evidence.service;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.exception.CrimeEvidenceDataException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.justice.laa.crime.evidence.service.IncomeEvidenceValidationService.CANNOT_APPLY_UPLIFT_IF_NO_OUTSTANDING_EVIDENCE_REQUIRED;
import static uk.gov.justice.laa.crime.evidence.service.IncomeEvidenceValidationService.CANNOT_CLEAR_UPLIFT_APPLIED_DATE;
import static uk.gov.justice.laa.crime.evidence.service.IncomeEvidenceValidationService.CANNOT_CLEAR_UPLIFT_REMOVED_DATE;
import static uk.gov.justice.laa.crime.evidence.service.IncomeEvidenceValidationService.CANNOT_MODIFY_UPLIFT_REMOVED_DATE;
import static uk.gov.justice.laa.crime.evidence.service.IncomeEvidenceValidationService.CANNOT_SET_NEW_UPLIFT_DATE;
import static uk.gov.justice.laa.crime.evidence.service.IncomeEvidenceValidationService.CANNOT_SET_UPLIFT_REMOVED_DATE_WHEN_NO_UPLIFT_APPLIED;
import static uk.gov.justice.laa.crime.evidence.service.IncomeEvidenceValidationService.MISSING_OTHER_EVIDENCE_DESCRIPTION;
import static uk.gov.justice.laa.crime.evidence.service.IncomeEvidenceValidationService.MUST_SET_UPLIFT_DATE_TO_TODAY;
import static uk.gov.justice.laa.crime.evidence.service.IncomeEvidenceValidationService.MUST_SET_UPLIFT_REMOVED_DATE_TO_TODAY;

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
        assertThrows(CrimeEvidenceDataException.class, () ->
                incomeEvidenceValidationService.checkEvidenceReceivedDate(futureDate, currentDate)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceReceivedDateIsInvokedWithIncomeEvidenceReceivedDateBeforeCurrentDate_thenExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        LocalDate currentDate = LocalDate.now();
        assertThrows(CrimeEvidenceDataException.class, () ->
                incomeEvidenceValidationService.checkEvidenceReceivedDate(pastDate, currentDate)
        );
    }

    @Test
    void givenValidDatesAndNoEvidencePending_whenCheckEvidenceDueDatesIsInvoked_thenNoExceptionIsThrown() {
        incomeEvidenceValidationService.checkEvidenceDueDates(LocalDate.now(), LocalDate.now(), false);
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedWithEvidenceDueDateNullAndEvidencePending_thenExceptionIsThrown() {
        LocalDate currentDate = LocalDate.now();
        assertThrows(CrimeEvidenceDataException.class, () ->
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
        assertThrows(CrimeEvidenceDataException.class, () ->
                incomeEvidenceValidationService.checkEvidenceDueDates(pastDate, currentDate1, false)
        );
    }

    @Test
    void givenValidDates_whenCheckEvidenceDueDatesIsInvokedToAttemptToSetDueDateInThePast_thenExceptionIsThrown() {
        LocalDate pastDate = LocalDate.now().minusMonths(2);
        assertThrows(CrimeEvidenceDataException.class, () ->
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
        assertThatThrownBy(() -> incomeEvidenceValidationService.checkExtraEvidenceDescriptions(evidenceList))
            .isInstanceOf(CrimeEvidenceDataException.class)
            .hasMessage(MISSING_OTHER_EVIDENCE_DESCRIPTION);
    }

    @Test
    void givenIncomeExtraEvidenceWithEmptyDescription_whenCheckExtraEvidenceDescriptionsIsInvoked_thenExceptionIsThrown() {
        ApiIncomeEvidence extraIncomeEvidence = TestModelDataBuilder.getIncomeEvidence(IncomeEvidenceType.OTHER_BUSINESS);
        extraIncomeEvidence.setDescription("");
        List<ApiIncomeEvidence> evidenceList = List.of(extraIncomeEvidence);
        assertThatThrownBy(() -> incomeEvidenceValidationService.checkExtraEvidenceDescriptions(evidenceList))
            .isInstanceOf(CrimeEvidenceDataException.class)
            .hasMessage(MISSING_OTHER_EVIDENCE_DESCRIPTION);
    }

    @Test
    void givenIncomeEvidence_whenCheckExtraEvidenceDescriptionsIsInvoked_thenNoExceptionIsThrown() {
        ApiIncomeEvidence incomeEvidence = TestModelDataBuilder.getIncomeEvidence(IncomeEvidenceType.NINO);
        incomeEvidence.setDescription("");
        incomeEvidenceValidationService.checkExtraEvidenceDescriptions(List.of(incomeEvidence));
    }

    @ParameterizedTest
    @MethodSource("validateUpliftDates")
    void testValidateUpliftDatesExceptions(UpdateEvidenceDTO updateEvidenceDTO, boolean allEvidenceReceived, String message) {
        assertThatThrownBy(() -> incomeEvidenceValidationService.validateUpliftDates(updateEvidenceDTO, allEvidenceReceived))
            .isInstanceOf(CrimeEvidenceDataException.class)
            .hasMessage(message);
    }

    public static Stream<Arguments> validateUpliftDates() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        return Stream.of(
            Arguments.of(UpdateEvidenceDTO.builder().upliftRemovedDate(today).build(),
                true,
                CANNOT_SET_UPLIFT_REMOVED_DATE_WHEN_NO_UPLIFT_APPLIED),
            Arguments.of(UpdateEvidenceDTO.builder().upliftRemovedDate(yesterday).oldUpliftAppliedDate(lastMonth).oldUpliftRemovedDate(lastMonth).build(),
                true,
                MUST_SET_UPLIFT_REMOVED_DATE_TO_TODAY),
            Arguments.of(UpdateEvidenceDTO.builder().oldUpliftRemovedDate(lastMonth).build(),
                true,
                CANNOT_CLEAR_UPLIFT_REMOVED_DATE),
            Arguments.of(UpdateEvidenceDTO.builder().upliftRemovedDate(today).oldUpliftAppliedDate(lastMonth).oldUpliftRemovedDate(lastMonth).build(),
                true,
                CANNOT_MODIFY_UPLIFT_REMOVED_DATE),
            Arguments.of(UpdateEvidenceDTO.builder().upliftAppliedDate(today).build(),
                true,
                CANNOT_APPLY_UPLIFT_IF_NO_OUTSTANDING_EVIDENCE_REQUIRED),
            Arguments.of(UpdateEvidenceDTO.builder().upliftAppliedDate(yesterday).build(),
                false,
                MUST_SET_UPLIFT_DATE_TO_TODAY),
            Arguments.of(UpdateEvidenceDTO.builder().oldUpliftAppliedDate(lastMonth).build(),
                false,
                CANNOT_CLEAR_UPLIFT_APPLIED_DATE),
            Arguments.of(UpdateEvidenceDTO.builder().oldUpliftAppliedDate(lastMonth).upliftAppliedDate(today).build(),
                false,
                CANNOT_SET_NEW_UPLIFT_DATE)
        );
    }
}