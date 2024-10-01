package uk.gov.justice.laa.crime.evidence.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EvidenceDTO {
        /**
         * This is a basic DTO with the required fields for validation. When we migrate the remaining logic
         * we can update this DTO with any additional fields we need
        */
        private LocalDateTime incomeEvidenceReceivedDate;
        private LocalDateTime applicationReceivedDate;
        private String incomeExtraEvidence;
        private String incomeExtraEvidenceText;
        private LocalDateTime evidenceDueDate;
        private LocalDateTime firstReminderDate;
        private LocalDateTime secondReminderDate;
        private LocalDateTime existingEvidenceDueDate;
}
