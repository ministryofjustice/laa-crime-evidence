package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.OtherEvidenceTypes;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeEvidenceValidationService {

    public void checkEvidenceReceivedDate(Date incomeEvidenceReceivedDate, Date applicationReceivedDate) {
        Date currentDate = DateUtil.getCurrentDate();
        if (incomeEvidenceReceivedDate != null && incomeEvidenceReceivedDate.after(currentDate)) {
            throw new IllegalArgumentException("Income evidence received date cannot be in the future");
        }

        if (incomeEvidenceReceivedDate != null && incomeEvidenceReceivedDate.before(applicationReceivedDate)) {
            throw new IllegalArgumentException("Income evidence received date cannot be before application date received");
        }
    }


    public void checkExtraEvidenceDescription(String incomeExtraEvidence, String incomeExtraEvidenceText) {
        if (OtherEvidenceTypes.getFrom(incomeExtraEvidence) != null && StringUtils.isBlank(incomeExtraEvidenceText)) {
            throw new IllegalArgumentException("When other evidence is requested, you must provide descriptive text.");
        }
    }


    public void checkEvidenceDueDates(Date evidenceDueDate, Date firstReminderDate, Date secondReminderDate,
                                      Date existingEvidenceDueDate) {
        Date currentDate = DateUtil.getCurrentDate();
        if ((evidenceDueDate == null && existingEvidenceDueDate != null) && (
                firstReminderDate != null || secondReminderDate != null)) {
            throw new IllegalArgumentException("Evidence due date cannot be null");
        }

        if (evidenceDueDate != null && evidenceDueDate.before(currentDate)
                && (!evidenceDueDate.equals(existingEvidenceDueDate))) {
            throw new IllegalArgumentException("Cannot set due date in the past.");
        }
    }
}
