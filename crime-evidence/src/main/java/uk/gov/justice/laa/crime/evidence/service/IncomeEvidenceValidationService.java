package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.exception.CrimeEvidenceDataException;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeEvidenceValidationService {
    private static final List<IncomeEvidenceType> EXTRA_EVIDENCES = List.of(IncomeEvidenceType.OTHER,
            IncomeEvidenceType.OTHER_ADHOC,
            IncomeEvidenceType.OTHER_BUSINESS);
    public static final String MISSING_OTHER_EVIDENCE_DESCRIPTION =
            "When other evidence is requested, you must provide descriptive text.";
    public static final String CANNOT_SET_UPLIFT_REMOVED_DATE_WHEN_NO_UPLIFT_APPLIED =
            "Cannot set uplift removed date when no uplift applied";
    public static final String MUST_SET_UPLIFT_REMOVED_DATE_TO_TODAY = "Must set uplift removed date to today.";
    public static final String CANNOT_CLEAR_UPLIFT_REMOVED_DATE =
            "Cannot clear uplift removed date. Set new uplift applied date instead.";
    public static final String CANNOT_MODIFY_UPLIFT_REMOVED_DATE = "Cannot modify uplift removed date.";
    public static final String CANNOT_APPLY_UPLIFT_IF_NO_OUTSTANDING_EVIDENCE_REQUIRED =
            "Cannot apply uplift if no outstanding evidence required.";
    public static final String MUST_SET_UPLIFT_DATE_TO_TODAY = "Must set uplift date to today.";
    public static final String CANNOT_CLEAR_UPLIFT_APPLIED_DATE =
            "Cannot clear uplift applied date. Set uplift removed date instead.";
    public static final String CANNOT_SET_NEW_UPLIFT_DATE = "Cannot set new uplift date if existing one is not removed.";

    public void checkEvidenceReceivedDate(LocalDate incomeEvidenceReceivedDate, LocalDate applicationReceivedDate) {
        LocalDate currentDate = LocalDate.now();
        if (incomeEvidenceReceivedDate != null && incomeEvidenceReceivedDate.isAfter(currentDate)) {
            throw new CrimeEvidenceDataException("Income evidence received date cannot be in the future");
        }

        if (incomeEvidenceReceivedDate != null && incomeEvidenceReceivedDate.isBefore(applicationReceivedDate)) {
            throw new CrimeEvidenceDataException("Income evidence received date cannot be before application date received");
        }
    }

    public void checkEvidenceDueDates(LocalDate evidenceDueDate, LocalDate existingEvidenceDueDate, boolean evidencePending) {
        LocalDate currentDate = LocalDate.now();
        if (evidenceDueDate == null && existingEvidenceDueDate != null && evidencePending) {
            throw new CrimeEvidenceDataException("Evidence due date cannot be null");
        }

        if (evidenceDueDate != null && evidenceDueDate.isBefore(currentDate)
                && (!evidenceDueDate.equals(existingEvidenceDueDate))) {
            throw new CrimeEvidenceDataException("Cannot set due date in the past.");
        }
    }

    public void checkExtraEvidenceDescriptions(List<ApiIncomeEvidence> incomeEvidences) {
        incomeEvidences.stream()
            .filter(apiIncomeEvidence -> EXTRA_EVIDENCES.contains(apiIncomeEvidence.getEvidenceType()))
            .forEach(apiIncomeEvidence -> {
                    if (StringUtils.isBlank(apiIncomeEvidence.getDescription())) {
                        throw new CrimeEvidenceDataException(MISSING_OTHER_EVIDENCE_DESCRIPTION);
                    }
                }
            );
    }

    public void validateUpliftDates(UpdateEvidenceDTO updateEvidenceDTO, boolean allEvidenceReceived) {
        LocalDate upliftAppliedDate = updateEvidenceDTO.getUpliftAppliedDate();
        LocalDate upliftRemovedDate = updateEvidenceDTO.getUpliftRemovedDate();
        LocalDate oldUpliftAppliedDate = updateEvidenceDTO.getOldUpliftAppliedDate();
        LocalDate oldUpliftRemovedDate = updateEvidenceDTO.getOldUpliftRemovedDate();

        validateUpliftRemovedDate(upliftRemovedDate, oldUpliftAppliedDate, oldUpliftRemovedDate);

        validateUpliftAppliedDate(allEvidenceReceived, upliftAppliedDate, oldUpliftAppliedDate, oldUpliftRemovedDate);
    }

    private static void validateUpliftRemovedDate(LocalDate upliftRemovedDate, LocalDate oldUpliftAppliedDate, LocalDate oldUpliftRemovedDate) {
        if (upliftRemovedDate != null) {
            if (oldUpliftAppliedDate == null) {
                throw new CrimeEvidenceDataException(CANNOT_SET_UPLIFT_REMOVED_DATE_WHEN_NO_UPLIFT_APPLIED);
            }

            if (!upliftRemovedDate.equals(oldUpliftRemovedDate) && !upliftRemovedDate.equals(LocalDate.now())) {
                throw new CrimeEvidenceDataException(MUST_SET_UPLIFT_REMOVED_DATE_TO_TODAY);
            }
        }

        if (oldUpliftRemovedDate != null) {
            if (upliftRemovedDate == null) {
                throw new CrimeEvidenceDataException(CANNOT_CLEAR_UPLIFT_REMOVED_DATE);
            } else if (!upliftRemovedDate.equals(oldUpliftRemovedDate)) {
                throw new CrimeEvidenceDataException(CANNOT_MODIFY_UPLIFT_REMOVED_DATE);
            }
        }
    }

    private static void validateUpliftAppliedDate(boolean allEvidenceReceived, LocalDate upliftAppliedDate, LocalDate oldUpliftAppliedDate, LocalDate oldUpliftRemovedDate) {
        if (upliftAppliedDate != null) {
            if (oldUpliftAppliedDate == null && allEvidenceReceived) {
                throw new CrimeEvidenceDataException(CANNOT_APPLY_UPLIFT_IF_NO_OUTSTANDING_EVIDENCE_REQUIRED);
            }

            if (!upliftAppliedDate.equals(oldUpliftAppliedDate) && !upliftAppliedDate.equals(LocalDate.now())) {
                throw new CrimeEvidenceDataException(MUST_SET_UPLIFT_DATE_TO_TODAY);
            }
        }

        if (oldUpliftAppliedDate != null) {
            if (upliftAppliedDate == null) {
                throw new CrimeEvidenceDataException(CANNOT_CLEAR_UPLIFT_APPLIED_DATE);
            }
            if (oldUpliftRemovedDate == null && !upliftAppliedDate.equals(oldUpliftAppliedDate)) {
                throw new CrimeEvidenceDataException(CANNOT_SET_NEW_UPLIFT_DATE);
            }
        }
    }

}
