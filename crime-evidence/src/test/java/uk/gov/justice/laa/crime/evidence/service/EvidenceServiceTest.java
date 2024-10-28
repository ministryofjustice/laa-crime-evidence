package uk.gov.justice.laa.crime.evidence.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiApplicantDetails;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidenceItems;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.common.Constants;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeResponse;
import uk.gov.justice.laa.crime.enums.EvidenceFeeLevel;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.ApplicantType;
import uk.gov.justice.laa.crime.util.DateUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class EvidenceServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private EvidenceService evidenceService;

    @Mock
    private IncomeEvidenceService incomeEvidenceService;

    @Mock
    private IncomeEvidenceValidationService incomeEvidenceValidationService;

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenValidCrimeEvidence_whenCalculateEvidenceFeeIsInvoked_validResponseIsReturned() {
        CrimeEvidenceDTO requestDTO = TestModelDataBuilder.getCrimeEvidenceDTO();
        when(maatCourtDataService.getRepOrderCapitalByRepId(anyInt()))
                .thenReturn(2L);
        ApiCalculateEvidenceFeeResponse response = evidenceService.calculateEvidenceFee(requestDTO);

        softly.assertThat(response.getEvidenceFee().getDescription())
                .isEqualTo(EvidenceFeeLevel.LEVEL1.getDescription());

        softly.assertThat(response.getEvidenceFee().getFeeLevel())
                .isEqualTo(EvidenceFeeLevel.LEVEL1.getFeeLevel());
        softly.assertAll();
    }

    @Test
    void givenCrimeEvidenceWithNoCapitalEvidence_whenCalculateEvidenceFeeIsInvoked_validResponseIsReturned() {
        CrimeEvidenceDTO requestDTO = TestModelDataBuilder.getCrimeEvidenceDTO();
        requestDTO.setCapitalEvidence(null);
        ApiCalculateEvidenceFeeResponse response = evidenceService.calculateEvidenceFee(requestDTO);

        assertThat(response.getEvidenceFee()).isNull();
    }

    @Test
    void givenCrimeEvidenceWithNoCapitalEvidenceReceivedDate_whenCalculateEvidenceFeeIsInvoked_validResponseIsReturned() {
        CrimeEvidenceDTO requestDTO = TestModelDataBuilder.getCrimeEvidenceDTO();
        requestDTO.setCapitalEvidenceReceivedDate(null);
        ApiCalculateEvidenceFeeResponse response = evidenceService.calculateEvidenceFee(requestDTO);

        assertThat(response.getEvidenceFee()).isNull();
    }

    @Test
    void givenCrimeEvidenceWithNoIncomeEvidenceReceivedDate_whenCalculateEvidenceFeeIsInvoked_validResponseIsReturned() {
        CrimeEvidenceDTO requestDTO = TestModelDataBuilder.getCrimeEvidenceDTO();
        requestDTO.setIncomeEvidenceReceivedDate(null);
        ApiCalculateEvidenceFeeResponse response = evidenceService.calculateEvidenceFee(requestDTO);

        assertThat(response.getEvidenceFee()).isNull();
    }

    @Test
    void givenCommittedMagCourtOutcome_whenCalculateEvidenceFeeIsInvoked_validResponseIsReturned() {
        CrimeEvidenceDTO requestDTO = TestModelDataBuilder.getCrimeEvidenceDTO();
        requestDTO.setMagCourtOutcome("COMMITTED");
        ApiCalculateEvidenceFeeResponse response = evidenceService.calculateEvidenceFee(requestDTO);

        assertThat(response.getEvidenceFee()).isNull();
    }

    @Test
    void givenSentForTrialAndEvidenceFeeLevelIsNull_WhenIsCalcRequired_TrueIsReturned() {
        CrimeEvidenceDTO requestDTO = TestModelDataBuilder.getCrimeEvidenceDTO();
        assertThat(evidenceService.isCalcRequired(requestDTO)).isTrue();
    }

    @Test
    void givenSentForTrialAndEvidenceFeeIsNull_WhenIsCalcRequired_TrueIsReturned() {
        CrimeEvidenceDTO requestDTO = TestModelDataBuilder.getCrimeEvidenceDTO();
        requestDTO.setEvidenceFee(null);
        assertThat(evidenceService.isCalcRequired(requestDTO)).isTrue();
    }

    @Test
    void givenCommittedForTrialAndEvidenceFeeLevelIsNull_WhenIsCalcRequired_TrueIsReturned() {
        CrimeEvidenceDTO requestDTO = TestModelDataBuilder.getCrimeEvidenceDTO();
        requestDTO.setMagCourtOutcome(Constants.COMMITTED_FOR_TRIAL);
        assertThat(evidenceService.isCalcRequired(requestDTO)).isTrue();
    }

    @Test
    void givenCommittedAndEvidenceFeeLevelIsNull_WhenIsCalcRequired_FalseIsReturned() {
        CrimeEvidenceDTO requestDTO = TestModelDataBuilder.getCrimeEvidenceDTO();
        requestDTO.setMagCourtOutcome("COMMITTED");
        assertThat(evidenceService.isCalcRequired(requestDTO)).isFalse();
    }

    @Test
    void givenCommittedForTrialAndEvidenceFeeLevelIsNotNull_WhenIsCalcRequired_FalseIsReturned() {
        CrimeEvidenceDTO requestDTO = TestModelDataBuilder.getCrimeEvidenceDTO();
        requestDTO.getEvidenceFee().setFeeLevel(EvidenceFeeLevel.LEVEL1.getDescription());
        assertThat(evidenceService.isCalcRequired(requestDTO)).isFalse();
    }

    @Test
    void givenNoEvidenceItems_whenUpdateEvidenceIsInvoked_thenExceptionIsThrown() {
        UpdateEvidenceDTO updateEvidenceDTO = TestModelDataBuilder.getUpdateEvidenceRequest();

        assertThatThrownBy(() -> evidenceService.updateEvidence(updateEvidenceDTO))
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

        assertThatThrownBy(() -> evidenceService.updateEvidence(updateEvidenceDTO))
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

        assertThatThrownBy(() -> evidenceService.updateEvidence(updateEvidenceDTO))
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

        when(incomeEvidenceService.checkEvidenceReceived(
            eq(updateEvidenceDTO.getApplicantIncomeEvidenceItems()),
            any(),
            any(),
            any(),
            any(),
            eq(ApplicantType.APPLICANT)))
            .thenReturn(false);

        ApiUpdateIncomeEvidenceResponse expectedResponse = new ApiUpdateIncomeEvidenceResponse()
            .withApplicantEvidenceItems(new ApiIncomeEvidenceItems(applicantDetails, applicantEvidenceItems))
            .withDueDate(evidenceDueDate)
            .withAllEvidenceReceivedDate(null);

        ApiUpdateIncomeEvidenceResponse actualResponse = evidenceService.updateEvidence(updateEvidenceDTO);

        Assertions.assertNull(updateEvidenceDTO.getEvidenceReceivedDate());
        Assertions.assertEquals(expectedResponse, actualResponse);
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

        when(incomeEvidenceService.checkEvidenceReceived(
            eq(updateEvidenceDTO.getPartnerIncomeEvidenceItems()),
            any(),
            any(),
            any(),
            any(),
            eq(ApplicantType.PARTNER)))
            .thenReturn(true);

        ApiUpdateIncomeEvidenceResponse expectedResponse = new ApiUpdateIncomeEvidenceResponse()
            .withApplicantEvidenceItems(new ApiIncomeEvidenceItems(applicantDetails, Collections.emptyList()))
            .withPartnerEvidenceItems(new ApiIncomeEvidenceItems(partnerDetails, partnerEvidenceItems))
            .withDueDate(evidenceDueDate)
            .withAllEvidenceReceivedDate(evidenceReceivedDate);

        ApiUpdateIncomeEvidenceResponse actualResponse = evidenceService.updateEvidence(updateEvidenceDTO);

        Assertions.assertNotNull(updateEvidenceDTO.getEvidenceReceivedDate());
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    private ApiApplicantDetails buildApplicantDetails(int applicantId, EmploymentStatus employmentStatus) {
        ApiApplicantDetails applicantDetails = new ApiApplicantDetails();
        applicantDetails.setId(applicantId);
        applicantDetails.setEmploymentStatus(employmentStatus);

        return applicantDetails;
    }
}