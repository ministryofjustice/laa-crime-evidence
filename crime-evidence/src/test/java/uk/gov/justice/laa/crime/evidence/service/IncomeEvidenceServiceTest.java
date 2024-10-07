package uk.gov.justice.laa.crime.evidence.service;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRepository;
import uk.gov.justice.laa.crime.evidence.staticdata.entity.IncomeEvidenceRequiredItemEntity;

@ExtendWith(MockitoExtension.class)
public class IncomeEvidenceServiceTest {

    @Mock
    private IncomeEvidenceRepository incomeEvidenceRepository;

    @Test
    void givenNoEvidenceItems_whenIsRequiredEvidenceOutstandingIsInvoked_thenReturnFalse() {
        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRepository);

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(Collections.emptyList());

        Assertions.assertFalse(result);
    }

    @Test
    void givenEvidenceItemsThatAreNotRequired_whenIsRequiredEvidenceOutstandingIsInvoked_thenReturnFalse() {
        when(incomeEvidenceRepository.findByIds(Arrays.asList(2))).thenReturn(
            Arrays.asList(
                new IncomeEvidenceRequiredItemEntity(2, 2, "N", "test", LocalDateTime.now(), "test", LocalDateTime.now())
            )
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRepository);

        List<ApiIncomeEvidence> evidenceItems = List.of(
            new ApiIncomeEvidence(1, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"),
            new ApiIncomeEvidence(2, null, IncomeEvidenceType.BANK_STATEMENT, false, "Bank statement")
        );

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(evidenceItems);

        Assertions.assertFalse(result);
    }

    @Test
    void givenAtLeastOneRequiredEvidenceItemNotReceived_whenIsRequiredEvidenceOutstandingIsInvoked_thenReturnTrue() {
        when(incomeEvidenceRepository.findByIds(Arrays.asList(2, 3))).thenReturn(
            Arrays.asList(
                new IncomeEvidenceRequiredItemEntity(2, 2, "N", "test", LocalDateTime.now(), "test", LocalDateTime.now()),
                new IncomeEvidenceRequiredItemEntity(3, 3, "Y", "test", LocalDateTime.now(), "test", LocalDateTime.now())
            )
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRepository);

        List<ApiIncomeEvidence> evidenceItems = List.of(
            new ApiIncomeEvidence(1, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"),
            new ApiIncomeEvidence(2, null, IncomeEvidenceType.BANK_STATEMENT, false, "Bank statement"),
            new ApiIncomeEvidence(3, null, IncomeEvidenceType.EMP_LETTER, true, "Employment letter")
        );

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(evidenceItems);

        Assertions.assertTrue(result);
    }


}
