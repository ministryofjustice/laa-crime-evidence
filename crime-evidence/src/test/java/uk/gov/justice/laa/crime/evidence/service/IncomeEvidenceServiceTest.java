package uk.gov.justice.laa.crime.evidence.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidenceItems;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;

@ExtendWith(MockitoExtension.class)
public class IncomeEvidenceServiceTest {

    @Test
    void givenNoEvidenceItems_whenIsRequiredEvidenceOutstandingIsInvoked_thenReturnFalse() {
        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService();

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(Collections.emptyList());

        Assertions.assertFalse(result);
    }

    @Test
    void givenEvidenceItemsThatAreNotRequired_whenIsRequiredEvidenceOutstandingIsInvoked_thenReturnFalse() {
        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService();

        List<ApiIncomeEvidence> evidenceItems = List.of(
            new ApiIncomeEvidence(1, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"),
            new ApiIncomeEvidence(2, null, IncomeEvidenceType.BANK_STATEMENT, false, "Bank statement")
        );

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(evidenceItems);

        Assertions.assertFalse(result);
    }

    @Test
    void givenAtLeastOneRequiredEvidenceItemNotReceived_whenIsRequiredEvidenceOutstandingIsInvoked_thenReturnTrue() {
        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService();

        List<ApiIncomeEvidence> evidenceItems = List.of(
            new ApiIncomeEvidence(1, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"),
            new ApiIncomeEvidence(2, null, IncomeEvidenceType.BANK_STATEMENT, false, "Bank statement"),
            new ApiIncomeEvidence(3, null, IncomeEvidenceType.EMP_LETTER, true, "Employment letter")
        );

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(evidenceItems);

        Assertions.assertTrue(result);
    }
}
