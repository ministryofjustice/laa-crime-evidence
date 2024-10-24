package uk.gov.justice.laa.crime.evidence.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceDTO;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.OtherEvidenceTypes;
import uk.gov.justice.laa.crime.util.DateUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeEvidenceValidationService {

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
            throw new IllegalArgumentException("When other evidence is requested, you must provide descriptive text.");
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
}
