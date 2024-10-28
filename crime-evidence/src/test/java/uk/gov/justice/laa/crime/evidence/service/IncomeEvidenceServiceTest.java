package uk.gov.justice.laa.crime.evidence.service;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceReceivedResultDTO;
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRequiredItemRepository;
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRequiredRepository;
import uk.gov.justice.laa.crime.evidence.staticdata.entity.IncomeEvidenceRequiredEntity;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.ApplicantType;
import uk.gov.justice.laa.crime.evidence.staticdata.projection.IncomeEvidenceRequiredItemProjection;

@ExtendWith(MockitoExtension.class)
class IncomeEvidenceServiceTest {

    @Mock
    private IncomeEvidenceRequiredRepository incomeEvidenceRequiredRepository;

    @Mock
    private IncomeEvidenceRequiredItemRepository incomeEvidenceRequiredItemRepository;

    @Test
    void givenNoRequiredEvidenceItemsExist_whenIsRequiredEvidenceOutstandingIsInvokedWithNoProvidedEvidenceItems_thenReturnFalse() {
        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(1)).thenReturn(
            List.of(
                createIncomeEvidenceRequiredItemProjection(34, "mock1", false),
                createIncomeEvidenceRequiredItemProjection(35, "mock2", false)
            )
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRequiredRepository, incomeEvidenceRequiredItemRepository);

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(1, Collections.emptyList());

        Assertions.assertFalse(result);
    }

    @Test
    void givenNoRequiredEvidenceItemsExist_whenIsRequiredEvidenceOutstandingIsInvokedWithProvidedEvidenceItems_thenReturnFalse() {
        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(1)).thenReturn(
            List.of(
                createIncomeEvidenceRequiredItemProjection(34, "mock1", false),
                createIncomeEvidenceRequiredItemProjection(35, "mock2", false)
            )
        );

        List<ApiIncomeEvidence> evidenceItems = List.of(
            new ApiIncomeEvidence(34, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"),
            new ApiIncomeEvidence(35, null, IncomeEvidenceType.BANK_STATEMENT, false, "Bank statement")
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRequiredRepository, incomeEvidenceRequiredItemRepository);

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(1, evidenceItems);

        Assertions.assertFalse(result);
    }

    @Test
    void givenRequiredEvidenceItemsAndNoEvidenceItemsProvided_whenIsRequiredEvidenceOutstandingIsInvoked_thenReturnTrue() {
        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(1)).thenReturn(
            List.of(
                createIncomeEvidenceRequiredItemProjection(34, "mock1", false),
                createIncomeEvidenceRequiredItemProjection(35, "mock2", true)
            )
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRequiredRepository, incomeEvidenceRequiredItemRepository);

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(1, Collections.emptyList());

        Assertions.assertTrue(result);
    }

    @Test
    void givenAtLeastOneRequiredEvidenceItemNotReceived_whenIsRequiredEvidenceOutstandingIsInvoked_thenReturnTrue() {
        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(2)).thenReturn(
            List.of(
                createIncomeEvidenceRequiredItemProjection(34, "mock1", false),
                createIncomeEvidenceRequiredItemProjection(35, "mock2", false),
                createIncomeEvidenceRequiredItemProjection(36, IncomeEvidenceType.EMP_LETTER.getName(), true)
            )
        );

        List<ApiIncomeEvidence> evidenceItems = List.of(
            new ApiIncomeEvidence(1, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"),
            new ApiIncomeEvidence(2, null, IncomeEvidenceType.BANK_STATEMENT, false, "Bank statement"),
            new ApiIncomeEvidence(3, null, IncomeEvidenceType.EMP_LETTER, true, "Employment letter")
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRequiredRepository, incomeEvidenceRequiredItemRepository);

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(2, evidenceItems);

        Assertions.assertTrue(result);
    }

    @Test
    void givenNoEvidenceItemsProvidedAndNoMinimumFoundInDatabaseQuery_whenCheckMinimumEvidenceItemsReceivedIsInvoked_thenReturnTrue() {
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(MagCourtOutcome.APPEAL_TO_CC.getOutcome(), EmploymentStatus.EMPLOY.getCode(), EmploymentStatus.EMPLOYED_CASH.getCode(), "APPLICANT", 0d)).thenReturn(null);

        List<ApiIncomeEvidence> providedEvidenceItems = Collections.emptyList();

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRequiredRepository, incomeEvidenceRequiredItemRepository);

        EvidenceReceivedResultDTO result = incomeEvidenceService.checkMinimumEvidenceItemsReceived(
            providedEvidenceItems,
            ApplicantType.APPLICANT,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO);

        Assertions.assertEquals(0, result.getMinimumEvidenceItemsRequired());
        Assertions.assertTrue(result.isEvidenceReceived());
    }

    @Test
    void givenNoEvidenceItemsProvidedAndZeroMinimumItemsRequired_whenCheckMinimumEvidenceItemsReceivedIsInvoked_thenReturnTrue() {
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(MagCourtOutcome.APPEAL_TO_CC.getOutcome(), EmploymentStatus.EMPLOY.getCode(), EmploymentStatus.EMPLOYED_CASH.getCode(), "APPLICANT", 0d)).thenReturn(
            IncomeEvidenceRequiredEntity.builder().evidenceItemsRequired(0).build()
        );

        List<ApiIncomeEvidence> providedEvidenceItems = Collections.emptyList();

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRequiredRepository, incomeEvidenceRequiredItemRepository);

        EvidenceReceivedResultDTO result = incomeEvidenceService.checkMinimumEvidenceItemsReceived(
            providedEvidenceItems,
            ApplicantType.APPLICANT,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO);

        Assertions.assertEquals(0, result.getMinimumEvidenceItemsRequired());
        Assertions.assertTrue(result.isEvidenceReceived());
    }

    @Test
    void givenEvidenceItemsProvidedAndMinimumIsMet_whenCheckMinimumEvidenceItemsReceivedIsInvoked_thenReturnTrue() {
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(MagCourtOutcome.APPEAL_TO_CC.getOutcome(), EmploymentStatus.EMPLOY.getCode(), EmploymentStatus.EMPLOYED_CASH.getCode(), "APPLICANT", 0d)).thenReturn(
            IncomeEvidenceRequiredEntity.builder().evidenceItemsRequired(2).build()
        );

        List<ApiIncomeEvidence> providedEvidenceItems = List.of(
            new ApiIncomeEvidence(1, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"),
            new ApiIncomeEvidence(2, LocalDate.of(2024, 9, 1), IncomeEvidenceType.BANK_STATEMENT, false, "Bank statement"),
            new ApiIncomeEvidence(3, LocalDate.of(2024, 9, 1), IncomeEvidenceType.EMP_LETTER, true, "Employment letter")
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRequiredRepository, incomeEvidenceRequiredItemRepository);

        EvidenceReceivedResultDTO result = incomeEvidenceService.checkMinimumEvidenceItemsReceived(
            providedEvidenceItems,
            ApplicantType.APPLICANT,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO);

        Assertions.assertEquals(2, result.getMinimumEvidenceItemsRequired());
        Assertions.assertTrue(result.isEvidenceReceived());
    }

    @Test
    void givenEvidenceItemsProvidedAndMinimumIsNotMet_whenCheckMinimumEvidenceItemsReceivedIsInvoked_thenReturnFalse() {
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(MagCourtOutcome.APPEAL_TO_CC.getOutcome(), EmploymentStatus.EMPLOY.getCode(), EmploymentStatus.EMPLOYED_CASH.getCode(), "APPLICANT", 0d)).thenReturn(
            IncomeEvidenceRequiredEntity.builder().evidenceItemsRequired(4).build()
        );

        List<ApiIncomeEvidence> providedEvidenceItems = List.of(
            new ApiIncomeEvidence(1, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"),
            new ApiIncomeEvidence(2, LocalDate.of(2024, 9, 1), IncomeEvidenceType.BANK_STATEMENT, false, "Bank statement"),
            new ApiIncomeEvidence(3, LocalDate.of(2024, 9, 1), IncomeEvidenceType.EMP_LETTER, true, "Employment letter")
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRequiredRepository, incomeEvidenceRequiredItemRepository);

        EvidenceReceivedResultDTO result = incomeEvidenceService.checkMinimumEvidenceItemsReceived(
            providedEvidenceItems,
            ApplicantType.APPLICANT,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO);

        Assertions.assertEquals(4, result.getMinimumEvidenceItemsRequired());
        Assertions.assertFalse(result.isEvidenceReceived());
    }


    @Test
    void givenMinimumEvidenceItemsNotMet_whenCheckEvidenceReceivedIsInvoked_thenReturnFalse() {
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(MagCourtOutcome.APPEAL_TO_CC.getOutcome(), EmploymentStatus.EMPLOY.getCode(), EmploymentStatus.EMPLOYED_CASH.getCode(), "APPLICANT", 0d)).thenReturn(
            IncomeEvidenceRequiredEntity.builder().evidenceItemsRequired(4).build()
        );

        List<ApiIncomeEvidence> applicantEvidenceItems = new ArrayList<>();

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRequiredRepository, incomeEvidenceRequiredItemRepository);

        boolean result = incomeEvidenceService.checkEvidenceReceived(
            applicantEvidenceItems,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO,
            ApplicantType.APPLICANT);

        Assertions.assertFalse(result);
    }

    @Test
    void givenOutstandingEvidence_whenCheckEvidenceReceivedIsInvoked_thenReturnFalse() {
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(MagCourtOutcome.APPEAL_TO_CC.getOutcome(), EmploymentStatus.EMPLOY.getCode(), EmploymentStatus.EMPLOYED_CASH.getCode(), "APPLICANT", 0d)).thenReturn(
            IncomeEvidenceRequiredEntity.builder()
                .id(2)
                .evidenceItemsRequired(1).build()
        );

        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(2)).thenReturn(
            List.of(
                createIncomeEvidenceRequiredItemProjection(34, "mock1", false),
                createIncomeEvidenceRequiredItemProjection(35, "mock2", false),
                createIncomeEvidenceRequiredItemProjection(36, "mock3", true)
            )
        );

        List<ApiIncomeEvidence> applicantEvidenceItems = List.of(
            new ApiIncomeEvidence(1, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"),
            new ApiIncomeEvidence(2, null, IncomeEvidenceType.BANK_STATEMENT, false, "Bank statement"),
            new ApiIncomeEvidence(3, null, IncomeEvidenceType.EMP_LETTER, true, "Employment letter")
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRequiredRepository, incomeEvidenceRequiredItemRepository);

        boolean result = incomeEvidenceService.checkEvidenceReceived(
            applicantEvidenceItems,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO,
            ApplicantType.APPLICANT);

        Assertions.assertFalse(result);
    }

    @Test
    void givenNoOutstandingEvidence_whenCheckEvidenceReceivedIsInvoked_thenReturnTrue() {
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(MagCourtOutcome.APPEAL_TO_CC.getOutcome(), EmploymentStatus.EMPLOY.getCode(), EmploymentStatus.EMPLOYED_CASH.getCode(), "APPLICANT", 0d)).thenReturn(
            IncomeEvidenceRequiredEntity.builder()
                .id(2)
                .evidenceItemsRequired(1).build()
        );

        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(2)).thenReturn(
            List.of(
                createIncomeEvidenceRequiredItemProjection(34, "mock1", false),
                createIncomeEvidenceRequiredItemProjection(35, "mock2", false),
                createIncomeEvidenceRequiredItemProjection(36, IncomeEvidenceType.EMP_LETTER.getName(), true)
            )
        );

        List<ApiIncomeEvidence> applicantEvidenceItems = List.of(
            new ApiIncomeEvidence(34, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"),
            new ApiIncomeEvidence(35, null, IncomeEvidenceType.BANK_STATEMENT, false, "Bank statement"),
            new ApiIncomeEvidence(36, LocalDate.of(2024, 9, 1), IncomeEvidenceType.EMP_LETTER, true, "Employment letter")
        );

        IncomeEvidenceService incomeEvidenceService = new IncomeEvidenceService(incomeEvidenceRequiredRepository, incomeEvidenceRequiredItemRepository);

        boolean result = incomeEvidenceService.checkEvidenceReceived(
            applicantEvidenceItems,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO,
            ApplicantType.APPLICANT);

        Assertions.assertTrue(result);
    }

    private static IncomeEvidenceRequiredItemProjection createIncomeEvidenceRequiredItemProjection(
        int id, String description, boolean mandatory)
    {
        return new IncomeEvidenceRequiredItemProjection() {
            @Override
            public int getId() {
                return id;
            }

            @Override
            public String getMandatory() {
                return mandatory ? "Y" : "N";
            }

            @Override
            public String getIncomeEvidenceRequiredDescription() {
                return description;
            }
        };
    }
}
