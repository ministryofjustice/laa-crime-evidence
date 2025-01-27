package uk.gov.justice.laa.crime.evidence.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiApplicantDetails;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidenceItems;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.dto.CreateEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceReceivedResultDTO;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRequiredItemRepository;
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRequiredRepository;
import uk.gov.justice.laa.crime.evidence.staticdata.entity.IncomeEvidenceRequiredEntity;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.ApplicantType;
import uk.gov.justice.laa.crime.evidence.staticdata.projection.IncomeEvidenceRequiredItemProjection;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncomeEvidenceServiceTest {

    @Mock
    private IncomeEvidenceRequiredRepository incomeEvidenceRequiredRepository;

    @Mock
    private IncomeEvidenceRequiredItemRepository incomeEvidenceRequiredItemRepository;

    @Mock
    private IncomeEvidenceValidationService incomeEvidenceValidationService;

    @InjectMocks
    private IncomeEvidenceService incomeEvidenceService;

    @Test
    void givenNoRequiredEvidenceItemsExist_whenIsRequiredEvidenceOutstandingIsInvokedWithNoProvidedEvidenceItems_thenReturnFalse() {
        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(1)).thenReturn(
            List.of(
                createIncomeEvidenceRequiredItemProjection(34, "mock1", false),
                createIncomeEvidenceRequiredItemProjection(35, "mock2", false)
            )
        );

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(1, Collections.emptyList());

        assertFalse(result);
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

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(1, evidenceItems);

        assertFalse(result);
    }

    @Test
    void givenRequiredEvidenceItemsAndNoEvidenceItemsProvided_whenIsRequiredEvidenceOutstandingIsInvoked_thenReturnTrue() {
        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(1)).thenReturn(
            List.of(
                createIncomeEvidenceRequiredItemProjection(34, "mock1", false),
                createIncomeEvidenceRequiredItemProjection(35, "mock2", true)
            )
        );

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(1, Collections.emptyList());

        assertTrue(result);
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

        boolean result = incomeEvidenceService.isRequiredEvidenceOutstanding(2, evidenceItems);

        assertTrue(result);
    }

    @Test
    void givenNoEvidenceItemsProvidedAndNoMinimumFoundInDatabaseQuery_whenCheckMinimumEvidenceItemsReceivedIsInvoked_thenReturnTrue() {
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(MagCourtOutcome.APPEAL_TO_CC.getOutcome(), EmploymentStatus.EMPLOY.getCode(), EmploymentStatus.EMPLOYED_CASH.getCode(), "APPLICANT", 0d)).thenReturn(null);

        List<ApiIncomeEvidence> providedEvidenceItems = Collections.emptyList();

        EvidenceReceivedResultDTO result = incomeEvidenceService.checkMinimumEvidenceItemsReceived(
            providedEvidenceItems,
            ApplicantType.APPLICANT,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO);

        assertEquals(0, result.getMinimumEvidenceItemsRequired());
        assertTrue(result.isEvidenceReceived());
    }

    @Test
    void givenNoEvidenceItemsProvidedAndZeroMinimumItemsRequired_whenCheckMinimumEvidenceItemsReceivedIsInvoked_thenReturnTrue() {
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(MagCourtOutcome.APPEAL_TO_CC.getOutcome(), EmploymentStatus.EMPLOY.getCode(), EmploymentStatus.EMPLOYED_CASH.getCode(), "APPLICANT", 0d)).thenReturn(
            IncomeEvidenceRequiredEntity.builder().evidenceItemsRequired(0).build()
        );

        List<ApiIncomeEvidence> providedEvidenceItems = Collections.emptyList();

        EvidenceReceivedResultDTO result = incomeEvidenceService.checkMinimumEvidenceItemsReceived(
            providedEvidenceItems,
            ApplicantType.APPLICANT,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO);

        assertEquals(0, result.getMinimumEvidenceItemsRequired());
        assertTrue(result.isEvidenceReceived());
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

        EvidenceReceivedResultDTO result = incomeEvidenceService.checkMinimumEvidenceItemsReceived(
            providedEvidenceItems,
            ApplicantType.APPLICANT,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO);

        assertEquals(2, result.getMinimumEvidenceItemsRequired());
        assertTrue(result.isEvidenceReceived());
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

        EvidenceReceivedResultDTO result = incomeEvidenceService.checkMinimumEvidenceItemsReceived(
            providedEvidenceItems,
            ApplicantType.APPLICANT,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO);

        assertEquals(4, result.getMinimumEvidenceItemsRequired());
        assertFalse(result.isEvidenceReceived());
    }


    @Test
    void givenMinimumEvidenceItemsNotMet_whenCheckEvidenceReceivedIsInvoked_thenReturnFalse() {
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(MagCourtOutcome.APPEAL_TO_CC.getOutcome(), EmploymentStatus.EMPLOY.getCode(), EmploymentStatus.EMPLOYED_CASH.getCode(), "APPLICANT", 0d)).thenReturn(
            IncomeEvidenceRequiredEntity.builder().evidenceItemsRequired(4).build()
        );

        List<ApiIncomeEvidence> applicantEvidenceItems = new ArrayList<>();

        boolean result = incomeEvidenceService.checkEvidenceReceived(
            applicantEvidenceItems,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO,
            ApplicantType.APPLICANT);

        assertFalse(result);
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

        boolean result = incomeEvidenceService.checkEvidenceReceived(
            applicantEvidenceItems,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO,
            ApplicantType.APPLICANT);

        assertFalse(result);
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

        boolean result = incomeEvidenceService.checkEvidenceReceived(
            applicantEvidenceItems,
            MagCourtOutcome.APPEAL_TO_CC,
            EmploymentStatus.EMPLOY,
            EmploymentStatus.EMPLOYED_CASH,
            BigDecimal.ZERO,
            ApplicantType.APPLICANT);

        assertTrue(result);
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

    @Test
    void givenNoEvidenceItems_whenUpdateEvidenceIsInvoked_thenExceptionIsThrown() {
        UpdateEvidenceDTO updateEvidenceDTO = TestModelDataBuilder.getUpdateEvidenceRequest();

        assertThatThrownBy(() -> incomeEvidenceService.updateEvidence(updateEvidenceDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No income evidence items provided");
    }

    @Test
    void givenValidationErrorOnEvidenceReceivedDate_whenUpdateEvidenceIsInvoked_thenExceptionIsThrown() {
        LocalDate applicationReceivedDate = LocalDate.of(2024, 8, 30);
        LocalDate evidenceItemReceivedDate = LocalDate.of(2024, 8, 29);
        LocalDate existingEvidenceDueDate = LocalDate.of(2024, 9, 7);

        List<ApiIncomeEvidence> applicantEvidenceItems = List.of(
                new ApiIncomeEvidence(1, evidenceItemReceivedDate, IncomeEvidenceType.ACCOUNTS, false, "Company accounts")
        );

        UpdateEvidenceDTO updateEvidenceDTO =  TestModelDataBuilder.getUpdateEvidenceRequest(
                applicationReceivedDate,
                null,
                applicantEvidenceItems,
                false,
                null,
                evidenceItemReceivedDate,
                existingEvidenceDueDate);

        doThrow(IllegalArgumentException.class).when(incomeEvidenceValidationService).checkEvidenceReceivedDate(
                DateUtil.parseLocalDate(updateEvidenceDTO.getEvidenceReceivedDate()), updateEvidenceDTO.getApplicationReceivedDate());

        assertThatThrownBy(() -> incomeEvidenceService.updateEvidence(updateEvidenceDTO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenValidationErrorOnEvidenceDueDate_whenUpdateEvidenceIsInvoked_thenExceptionIsThrown() {
        LocalDate applicationReceivedDate = LocalDate.of(2024, 8, 30);
        LocalDate evidenceItemReceivedDate = LocalDate.of(2024, 8, 31);
        LocalDate existingEvidenceDueDate = LocalDate.of(2024, 9, 7);

        List<ApiIncomeEvidence> applicantEvidenceItems = List.of(
                new ApiIncomeEvidence(1, evidenceItemReceivedDate, IncomeEvidenceType.ACCOUNTS, false, "Company accounts")
        );

        UpdateEvidenceDTO updateEvidenceDTO = TestModelDataBuilder.getUpdateEvidenceRequest(
                applicationReceivedDate,
                null,
                applicantEvidenceItems,
                true,
                null,
                evidenceItemReceivedDate,
                existingEvidenceDueDate);

        doThrow(IllegalArgumentException.class).when(incomeEvidenceValidationService).checkEvidenceDueDates(
                null,
                existingEvidenceDueDate,
                true);

        assertThatThrownBy(() -> incomeEvidenceService.updateEvidence(updateEvidenceDTO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenEvidenceHasNotBeenReceivedAndIncomeEvidenceHasReceivedDate_whenUpdateEvidenceIsInvoked_thenIncomeEvidenceReceivedDateIsRemoved() {
        LocalDate applicationReceivedDate = LocalDate.of(2024, 8, 30);
        LocalDate evidenceItemReceivedDate = LocalDate.of(2024, 9, 1);
        LocalDate evidenceDueDate = LocalDate.of(2024, 9, 30);
        LocalDate evidenceReceivedDate = LocalDate.of(2024, 9, 1);
        LocalDate existingEvidenceDueDate = LocalDate.of(2024, 9, 7);

        List<ApiIncomeEvidence> applicantEvidenceItems = List.of(
                new ApiIncomeEvidence(1, evidenceItemReceivedDate, IncomeEvidenceType.ACCOUNTS, false, "Company accounts")
        );

        ApiApplicantDetails applicantDetails = buildApplicantDetails(1, EmploymentStatus.EMPLOY);

        UpdateEvidenceDTO updateEvidenceDTO = TestModelDataBuilder.getUpdateEvidenceRequest(
                applicationReceivedDate,
                applicantDetails,
                applicantEvidenceItems,
                false,
                evidenceDueDate,
                evidenceReceivedDate,
                existingEvidenceDueDate);

        ApiUpdateIncomeEvidenceResponse expectedResponse = new ApiUpdateIncomeEvidenceResponse()
                .withApplicantEvidenceItems(new ApiIncomeEvidenceItems(applicantDetails, applicantEvidenceItems))
                .withDueDate(evidenceDueDate)
                .withAllEvidenceReceivedDate(null);
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(any(), any(), any(), any(), any()))
                .thenReturn(IncomeEvidenceRequiredEntity.builder().evidenceItemsRequired(3).build());

        ApiUpdateIncomeEvidenceResponse actualResponse = incomeEvidenceService.updateEvidence(updateEvidenceDTO);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void givenOnlyPartnerEvidenceIsProvided_whenUpdateEvidenceIsInvoked_thenIncomeEvidenceIsUpdated() {
        LocalDate applicationReceivedDate = LocalDate.of(2024, 8, 30);
        LocalDate evidenceItemReceivedDate = LocalDate.of(2024, 9, 1);
        LocalDate evidenceDueDate = LocalDate.of(2024, 9, 30);
        LocalDate evidenceReceivedDate = LocalDate.of(2024, 9, 1);
        LocalDate existingEvidenceDueDate = LocalDate.of(2024, 9, 7);

        List<ApiIncomeEvidence> partnerEvidenceItems = List.of(
                new ApiIncomeEvidence(1, evidenceItemReceivedDate, IncomeEvidenceType.ACCOUNTS, false, "Company accounts")
        );

        ApiApplicantDetails applicantDetails = buildApplicantDetails(1, EmploymentStatus.EMPLOY);
        ApiApplicantDetails partnerDetails = buildApplicantDetails(2, EmploymentStatus.EMPLOYED_CASH);

        UpdateEvidenceDTO updateEvidenceDTO = TestModelDataBuilder.getUpdateEvidenceRequest(
                applicationReceivedDate,
                applicantDetails,
                null,
                false,
                evidenceDueDate,
                evidenceReceivedDate,
                existingEvidenceDueDate);
        updateEvidenceDTO.setPartnerDetails(partnerDetails);
        updateEvidenceDTO.setPartnerIncomeEvidenceItems(partnerEvidenceItems);

        ApiUpdateIncomeEvidenceResponse expectedResponse = new ApiUpdateIncomeEvidenceResponse()
                .withApplicantEvidenceItems(new ApiIncomeEvidenceItems(applicantDetails, Collections.emptyList()))
                .withPartnerEvidenceItems(new ApiIncomeEvidenceItems(partnerDetails, partnerEvidenceItems))
                .withDueDate(evidenceDueDate)
                .withAllEvidenceReceivedDate(evidenceReceivedDate);

        ApiUpdateIncomeEvidenceResponse actualResponse = incomeEvidenceService.updateEvidence(updateEvidenceDTO);

        assertNotNull(updateEvidenceDTO.getEvidenceReceivedDate());
        assertEquals(expectedResponse, actualResponse);
    }

    private ApiApplicantDetails buildApplicantDetails(int applicantId, EmploymentStatus employmentStatus) {
        return new ApiApplicantDetails()
                .withId(applicantId)
                .withEmploymentStatus(employmentStatus);
    }

    @Test
    void givenValidCreateEvidenceDTO_whenCreateEvidenceIsInvoked_thenDefaultIncomeEvidenceIsCreated() {
        CreateEvidenceDTO createEvidenceDTO = TestModelDataBuilder.getCreateEvidenceRequest();
        createEvidenceDTO.setPartnerDetails(null);
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(any(), any(), any(), any(), any()))
                .thenReturn(IncomeEvidenceRequiredEntity.builder().evidenceItemsRequired(1).build());
        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(any()))
                .thenReturn(List.of(createIncomeEvidenceRequiredItemProjection(1, IncomeEvidenceType.NINO.getName(), true)));
        ApiIncomeEvidence apiIncomeEvidence = new ApiIncomeEvidence()
                .withEvidenceType(IncomeEvidenceType.NINO)
                .withMandatory(true);
        ApiCreateIncomeEvidenceResponse expectedResponse = new ApiCreateIncomeEvidenceResponse()
                .withApplicantEvidenceItems(getApiIncomeEvidenceItems(createEvidenceDTO.getApplicantDetails(), apiIncomeEvidence));

        assertEquals(expectedResponse, incomeEvidenceService.createEvidence(createEvidenceDTO));
    }

    @Test
    void givenValidCreateEvidenceDTOWithPartnerDetails_whenCreateEvidenceIsInvoked_thenDefaultIncomeEvidenceIsCreated() {
        CreateEvidenceDTO createEvidenceDTO = TestModelDataBuilder.getCreateEvidenceRequest();
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(any(), any(), any(), any(), any()))
                .thenReturn(IncomeEvidenceRequiredEntity.builder().evidenceItemsRequired(1).build());
        when(incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(any()))
                .thenReturn(List.of(createIncomeEvidenceRequiredItemProjection(1, IncomeEvidenceType.NINO.getName(), true)));
        ApiIncomeEvidence apiIncomeEvidence = new ApiIncomeEvidence()
                .withEvidenceType(IncomeEvidenceType.NINO)
                .withMandatory(true);
        ApiCreateIncomeEvidenceResponse expectedResponse = new ApiCreateIncomeEvidenceResponse()
                .withPartnerEvidenceItems(getApiIncomeEvidenceItems(createEvidenceDTO.getPartnerDetails(), apiIncomeEvidence))
                .withApplicantEvidenceItems(getApiIncomeEvidenceItems(createEvidenceDTO.getApplicantDetails(), apiIncomeEvidence));

        assertEquals(expectedResponse, incomeEvidenceService.createEvidence(createEvidenceDTO));

    }

    private static ApiIncomeEvidenceItems getApiIncomeEvidenceItems(ApiApplicantDetails apiApplicantDetails, ApiIncomeEvidence apiIncomeEvidence) {
        return new ApiIncomeEvidenceItems()
                .withApplicantDetails(apiApplicantDetails)
                .withIncomeEvidenceItems(List.of(apiIncomeEvidence));
    }

    @Test
    void givenAScenarioWhereNullEvidenceItemsReturned_whenCreateEvidenceIsInvoked_thenNoDefaultIncomeEvidenceIsCreated() {
        CreateEvidenceDTO createEvidenceDTO = TestModelDataBuilder.getCreateEvidenceRequest();
        createEvidenceDTO.setPartnerDetails(null);
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(any(), any(), any(), any(), any()))
                .thenReturn(null);
        ApiCreateIncomeEvidenceResponse expectedResponse = new ApiCreateIncomeEvidenceResponse()
                .withApplicantEvidenceItems(new ApiIncomeEvidenceItems()
                        .withApplicantDetails(createEvidenceDTO.getApplicantDetails()));

        assertEquals(expectedResponse, incomeEvidenceService.createEvidence(createEvidenceDTO));
    }

    @Test
    void givenAScenarioWhereZeroEvidenceItemsReturned_whenCreateEvidenceIsInvoked_thenNoDefaultIncomeEvidenceIsCreated() {
        CreateEvidenceDTO createEvidenceDTO = TestModelDataBuilder.getCreateEvidenceRequest();
        createEvidenceDTO.setPartnerDetails(null);
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(any(), any(), any(), any(), any()))
                .thenReturn(IncomeEvidenceRequiredEntity.builder().evidenceItemsRequired(0).build());
        ApiCreateIncomeEvidenceResponse expectedResponse = new ApiCreateIncomeEvidenceResponse()
                .withApplicantEvidenceItems(new ApiIncomeEvidenceItems()
                        .withApplicantDetails(createEvidenceDTO.getApplicantDetails()));

        assertEquals(expectedResponse, incomeEvidenceService.createEvidence(createEvidenceDTO));
    }

    @Test
    void givenUpliftAppliedDateIsModified_whenUpdateEvidenceIsInvoked_thenUpliftRemovedDateIsRemoved() {
        LocalDate applicationReceivedDate = LocalDate.of(2024, 8, 30);
        LocalDate evidenceDueDate = LocalDate.of(2024, 9, 30);

        LocalDate oldUpliftAppliedDate = LocalDate.of(2025, 1, 12);
        LocalDate oldUpliftRemovedDate = LocalDate.of(2025, 1, 15);
        LocalDate upliftAppliedDate = LocalDate.of(2025, 1, 20);

        List<ApiIncomeEvidence> applicantEvidenceItems = List.of(
                new ApiIncomeEvidence(1, null, IncomeEvidenceType.ACCOUNTS, false, "Company accounts")
        );

        ApiApplicantDetails applicantDetails = buildApplicantDetails(1, EmploymentStatus.EMPLOY);

        UpdateEvidenceDTO updateEvidenceDTO = UpdateEvidenceDTO.builder()
                .applicantIncomeEvidenceItems(applicantEvidenceItems)
                .partnerIncomeEvidenceItems(Collections.emptyList())
                .applicantDetails(applicantDetails)
                .magCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL)
                .applicantPensionAmount(BigDecimal.ZERO)
                .applicationReceivedDate(applicationReceivedDate)
                .evidenceDueDate(DateUtil.convertDateToDateTime(evidenceDueDate))
                .upliftAppliedDate(upliftAppliedDate)
                .oldUpliftAppliedDate(oldUpliftAppliedDate)
                .oldUpliftRemovedDate(oldUpliftRemovedDate)
                .upliftRemovedDate(oldUpliftRemovedDate)
                .build();

        ApiUpdateIncomeEvidenceResponse expectedResponse = new ApiUpdateIncomeEvidenceResponse()
                .withApplicantEvidenceItems(new ApiIncomeEvidenceItems(applicantDetails, applicantEvidenceItems))
                .withDueDate(evidenceDueDate)
                .withAllEvidenceReceivedDate(null)
                .withUpliftAppliedDate(upliftAppliedDate)
                .withUpliftRemovedDate(null);
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(any(), any(), any(), any(), any()))
                .thenReturn(IncomeEvidenceRequiredEntity.builder().evidenceItemsRequired(3).build());

        ApiUpdateIncomeEvidenceResponse actualResponse = incomeEvidenceService.updateEvidence(updateEvidenceDTO);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void givenAllEvidenceReceivedUpliftIsCurrentlyApplied_whenUpdateEvidenceIsInvoked_thenUpliftRemovedDateIsSet() {
        LocalDate applicationReceivedDate = LocalDate.of(2024, 8, 30);
        LocalDate evidenceDueDate = LocalDate.of(2024, 9, 30);
        LocalDate oldUpliftAppliedDate = LocalDate.of(2025, 1, 12);

        List<ApiIncomeEvidence> applicantEvidenceItems = List.of(
                new ApiIncomeEvidence(1, null, IncomeEvidenceType.ACCOUNTS, false, "Company accounts")
        );

        ApiApplicantDetails applicantDetails = buildApplicantDetails(1, EmploymentStatus.EMPLOY);

        UpdateEvidenceDTO updateEvidenceDTO = UpdateEvidenceDTO.builder()
                .applicantIncomeEvidenceItems(applicantEvidenceItems)
                .partnerIncomeEvidenceItems(Collections.emptyList())
                .applicantDetails(applicantDetails)
                .magCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL)
                .applicantPensionAmount(BigDecimal.ZERO)
                .applicationReceivedDate(applicationReceivedDate)
                .evidenceDueDate(DateUtil.convertDateToDateTime(evidenceDueDate))
                .evidenceReceivedDate(DateUtil.convertDateToDateTime(evidenceDueDate))
                .upliftAppliedDate(oldUpliftAppliedDate)
                .oldUpliftAppliedDate(oldUpliftAppliedDate)
                .build();

        ApiUpdateIncomeEvidenceResponse expectedResponse = new ApiUpdateIncomeEvidenceResponse()
                .withApplicantEvidenceItems(new ApiIncomeEvidenceItems(applicantDetails, applicantEvidenceItems))
                .withDueDate(evidenceDueDate)
                .withAllEvidenceReceivedDate(evidenceDueDate)
                .withUpliftAppliedDate(oldUpliftAppliedDate)
                .withUpliftRemovedDate(LocalDate.now());
        when(incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(any(), any(), any(), any(), any()))
                .thenReturn(IncomeEvidenceRequiredEntity.builder().evidenceItemsRequired(0).build());

        ApiUpdateIncomeEvidenceResponse actualResponse = incomeEvidenceService.updateEvidence(updateEvidenceDTO);

        assertEquals(expectedResponse, actualResponse);
    }
}
