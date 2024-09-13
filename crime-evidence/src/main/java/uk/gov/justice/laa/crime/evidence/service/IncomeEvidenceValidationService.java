package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.OtherEvidenceTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeEvidenceValidationService {

    public void checkEvidenceReceivedDate(Date incomeEvidenceReceivedDate, Date applicationReceivedDate) {
        Date currentDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        if (incomeEvidenceReceivedDate != null) {
            if (incomeEvidenceReceivedDate.after(currentDate)) {
                throw new IllegalArgumentException("Income evidence received date cannot be in the future");
            }
        }
        if (incomeEvidenceReceivedDate != null) {
            if (incomeEvidenceReceivedDate.before(applicationReceivedDate)) {
                throw new IllegalArgumentException("Income evidence received date cannot be before application date received");
            }
        }
    }

    public void checkExtraEvidenceDescription(String incomeExtraEvidence, String incomeExtraEvidenceText) {
        if (OtherEvidenceTypes.getFrom(incomeExtraEvidence) != null && StringUtils.isBlank(incomeExtraEvidenceText)) {
            throw new IllegalArgumentException("When other evidence is requested, you must provide descriptive text.");
        }
    }


    public void checkEvidenceDueDates(Date evidenceDueDate, Date firstReminderDate, Date secondReminderDate,
                                      Date existingEvidenceDueDate) {
        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if ((evidenceDueDate == null && existingEvidenceDueDate != null) && (
                firstReminderDate != null || secondReminderDate != null)) {
            throw new IllegalArgumentException("Evidence due date cannot be null");
        }

        if (evidenceDueDate != null && evidenceDueDate.before(date)
                && (!evidenceDueDate.equals(existingEvidenceDueDate) || existingEvidenceDueDate == null)) {
            throw new IllegalArgumentException("Cannot set due date in the past.");
        }
    }
}
