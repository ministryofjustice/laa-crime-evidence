package uk.gov.justice.laa.crime.evidence.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidenceSummary;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.common.Constants;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeResponse;
import uk.gov.justice.laa.crime.enums.EvidenceFeeLevel;
import uk.gov.justice.laa.crime.evidence.dto.UpdateEvidenceDTO;
import uk.gov.justice.laa.crime.util.DateUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class EvidenceServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private MeansAssessmentApiService meansAssessmentApiService;

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
    void givenNoEvidenceItems_whenUpdateEvidenceIsInvoked_thenEvidenceIsNotUpdated() {
        UpdateEvidenceDTO updateEvidenceDTO = TestModelDataBuilder.getUpdateEvidenceRequest();

        assertThatThrownBy(() -> evidenceService.updateEvidence(updateEvidenceDTO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("No income evidence items provided");
    }

    // givenMinimumApplicantEvidenceItemsNotReceived_whenUpdateEvidenceIsInvoked_thenEvidenceIsNotUpdated

    @Test
    void givenValidationErrorOnEvidenceReceivedDate_whenUpdateEvidenceIsInvoked_thenEvidenceIsNotUpdated() {
        UpdateEvidenceDTO updateEvidenceDTO = TestModelDataBuilder.getUpdateEvidenceRequest();
        updateEvidenceDTO.setApplicationReceivedDate(LocalDate.now());
        updateEvidenceDTO.setEvidenceReceivedDate(LocalDateTime.now().minusDays(1));
        updateEvidenceDTO.setApplicantIncomeEvidenceItems(List.of(
            new ApiIncomeEvidence(1, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"))
        );

        doThrow(IllegalArgumentException.class).when(incomeEvidenceValidationService).checkEvidenceReceivedDate(
            DateUtil.parseLocalDate(updateEvidenceDTO.getEvidenceReceivedDate()), updateEvidenceDTO.getApplicationReceivedDate());

        assertThatThrownBy(() -> evidenceService.updateEvidence(updateEvidenceDTO))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenValidationErrorOnEvidenceDueDate_whenUpdateEvidenceIsInvoked_thenEvidenceIsNotUpdated() {
        UpdateEvidenceDTO updateEvidenceDTO = TestModelDataBuilder.getUpdateEvidenceRequest();
        updateEvidenceDTO.setApplicationReceivedDate(LocalDate.now());
        updateEvidenceDTO.setEvidenceReceivedDate(LocalDateTime.now().minusDays(1));
        updateEvidenceDTO.setApplicantIncomeEvidenceItems(List.of(
            new ApiIncomeEvidence(1, LocalDate.of(2024, 9, 1), IncomeEvidenceType.ACCOUNTS, false, "Company accounts"))
        );

        LocalDateTime evidenceDueDate = LocalDateTime.now().plusMonths(1);
        LocalDateTime firstReminderDate = LocalDateTime.now().plusDays(7);

        ApiIncomeEvidenceSummary incomeEvidenceSummary = new ApiIncomeEvidenceSummary();
        incomeEvidenceSummary.setEvidenceDueDate(evidenceDueDate);
        incomeEvidenceSummary.setFirstReminderDate(firstReminderDate);

        ApiGetMeansAssessmentResponse getMeansAssessmentResponse = new ApiGetMeansAssessmentResponse();
        getMeansAssessmentResponse.setIncomeEvidenceSummary(incomeEvidenceSummary);

        when(meansAssessmentApiService.find(TestModelDataBuilder.FINANCIAL_ASSESSMENT_ID)).thenReturn(getMeansAssessmentResponse);

        doThrow(IllegalArgumentException.class).when(incomeEvidenceValidationService).checkEvidenceDueDates(
            null,
            DateUtil.parseLocalDate(firstReminderDate),
            null,
            DateUtil.parseLocalDate(evidenceDueDate));

        assertThatThrownBy(() -> evidenceService.updateEvidence(updateEvidenceDTO))
            .isInstanceOf(IllegalArgumentException.class);

        verify(meansAssessmentApiService, atLeastOnce()).find(TestModelDataBuilder.FINANCIAL_ASSESSMENT_ID);
    }
}