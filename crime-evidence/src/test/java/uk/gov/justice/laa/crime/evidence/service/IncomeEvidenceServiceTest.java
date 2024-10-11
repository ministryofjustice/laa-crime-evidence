package uk.gov.justice.laa.crime.evidence.service;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRequiredItemRepository;
import uk.gov.justice.laa.crime.evidence.staticdata.projection.IncomeEvidenceRequiredItemProjection;

@ExtendWith(MockitoExtension.class)
public class IncomeEvidenceServiceTest {

    @Mock
    private IncomeEvidenceRequiredItemRepository incomeEvidenceRequiredItemRepository;

    @Test
    void givenNoRequiredEvidenceItemsExist_whenIsRequiredEvidenceOutstandingIsInvokedWithNoProvidedEvidenceItems_thenReturnFalse() {
        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(1)).thenReturn(
            List.of(
                createIncomeEvidenceRequiredItemProjection(34, 1, false),
                createIncomeEvidenceRequiredItemProjection(35, 2, false)
            )
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(
            incomeEvidenceRequiredItemRepository);

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(1, Collections.emptyList());

        Assertions.assertFalse(result);
    }

    @Test
    void givenNoRequiredEvidenceItemsExist_whenIsRequiredEvidenceOutstandingIsInvokedWithProvidedEvidenceItems_thenReturnFalse() {
        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(1)).thenReturn(
            List.of(
                createIncomeEvidenceRequiredItemProjection(34, 1, false),
                createIncomeEvidenceRequiredItemProjection(35, 2, false)
            )
        );

        List<ApiIncomeEvidence> evidenceItems = List.of(
            new ApiIncomeEvidence(34, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"),
            new ApiIncomeEvidence(35, null, IncomeEvidenceType.BANK_STATEMENT, false, "Bank statement")
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(
            incomeEvidenceRequiredItemRepository);

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(1, evidenceItems);

        Assertions.assertFalse(result);
    }

    @Test
    void givenAtLeastOneRequiredEvidenceItemNotReceived_whenIsRequiredEvidenceOutstandingIsInvoked_thenReturnTrue() {
        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(2)).thenReturn(
            List.of(
                createIncomeEvidenceRequiredItemProjection(34, 1, false),
                createIncomeEvidenceRequiredItemProjection(35, 2, false),
                createIncomeEvidenceRequiredItemProjection(36, 2, true)
            )
        );

        List<ApiIncomeEvidence> evidenceItems = List.of(
            new ApiIncomeEvidence(1, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"),
            new ApiIncomeEvidence(2, null, IncomeEvidenceType.BANK_STATEMENT, false, "Bank statement"),
            new ApiIncomeEvidence(3, null, IncomeEvidenceType.EMP_LETTER, true, "Employment letter")
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(
            incomeEvidenceRequiredItemRepository);

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(2, evidenceItems);

        Assertions.assertTrue(result);
    }

    private static IncomeEvidenceRequiredItemProjection createIncomeEvidenceRequiredItemProjection(
        int id, int incomeEvidenceRequiredId, boolean mandatory)
    {
        return new IncomeEvidenceRequiredItemProjection() {
            @Override
            public int getId() {
                return id;
            }

            @Override
            public int getIncomeEvidenceRequiredId() {
                return incomeEvidenceRequiredId;
            }

            @Override
            public String getMandatory() {
                return mandatory ? "Y" : "N";
            }
        };
    }
}
