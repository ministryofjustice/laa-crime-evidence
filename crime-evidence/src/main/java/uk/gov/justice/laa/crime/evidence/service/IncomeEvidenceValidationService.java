package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceDTO;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.OtherEvidenceTypes;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeEvidenceValidationService {
    private static final List<IncomeEvidenceType> EXTRA_EVIDENCES = List.of(IncomeEvidenceType.OTHER,
            IncomeEvidenceType.OTHER_ADHOC,
            IncomeEvidenceType.OTHER_BUSINESS);
    public static final String MISSING_OTHER_EVIDENCE_DESCRIPTION = "When other evidence is requested, you must provide descriptive text.";

    public void validate(EvidenceDTO evidenceDTO){
        checkExtraEvidenceDescription(evidenceDTO.getIncomeExtraEvidence(),
                evidenceDTO.getIncomeExtraEvidenceText());

        checkEvidenceReceivedDate(
            DateUtil.parseLocalDate(evidenceDTO.getIncomeEvidenceReceivedDate()),
            DateUtil.parseLocalDate(evidenceDTO.getApplicationReceivedDate()));

        checkEvidenceDueDates(DateUtil.parseLocalDate(evidenceDTO.getEvidenceDueDate()),
                DateUtil.parseLocalDate(evidenceDTO.getFirstReminderDate()),
                DateUtil.parseLocalDate(evidenceDTO.getSecondReminderDate()),
                DateUtil.parseLocalDate(evidenceDTO.getExistingEvidenceDueDate()));
    }

    public void checkEvidenceReceivedDate(LocalDate incomeEvidenceReceivedDate, LocalDate applicationReceivedDate) {
        LocalDate currentDate = LocalDate.now();
        if (incomeEvidenceReceivedDate != null && incomeEvidenceReceivedDate.isAfter(currentDate)) {
            throw new IllegalArgumentException("Income evidence received date cannot be in the future");
        }

        if (incomeEvidenceReceivedDate != null && incomeEvidenceReceivedDate.isBefore(applicationReceivedDate)) {
            throw new IllegalArgumentException("Income evidence received date cannot be before application date received");
        }
    }

    public void checkExtraEvidenceDescription(String incomeExtraEvidence, String incomeExtraEvidenceText) {
        if (OtherEvidenceTypes.getFrom(incomeExtraEvidence) != null && StringUtils.isBlank(incomeExtraEvidenceText)) {
            throw new IllegalArgumentException(MISSING_OTHER_EVIDENCE_DESCRIPTION);
        }
    }

    public void checkEvidenceDueDates(LocalDate evidenceDueDate, LocalDate firstReminderDate, LocalDate secondReminderDate,
        LocalDate existingEvidenceDueDate) {
        LocalDate currentDate = LocalDate.now();
        if ((evidenceDueDate == null && existingEvidenceDueDate != null) && (
                firstReminderDate != null || secondReminderDate != null)) {
            throw new IllegalArgumentException("Evidence due date cannot be null");
        }

        if (evidenceDueDate != null && evidenceDueDate.isBefore(currentDate)
                && (!evidenceDueDate.equals(existingEvidenceDueDate))) {
            throw new IllegalArgumentException("Cannot set due date in the past.");
        }
    }

    public void checkExtraEvidenceDescriptions(List<ApiIncomeEvidence> incomeEvidences) {
        incomeEvidences.stream()
            .filter(apiIncomeEvidence -> EXTRA_EVIDENCES.contains(apiIncomeEvidence.getEvidenceType()))
            .forEach(apiIncomeEvidence -> {
                    if (StringUtils.isBlank(apiIncomeEvidence.getDescription())) {
                        throw new IllegalArgumentException(MISSING_OTHER_EVIDENCE_DESCRIPTION);
                    }
                }
            );
    }
}
