package uk.gov.justice.laa.crime.evidence.service;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCalculateEvidenceFeeResponse;
import uk.gov.justice.laa.crime.enums.EvidenceFeeLevel;
import uk.gov.justice.laa.crime.evidence.common.Constants;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.dto.CrimeEvidenceDTO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class EvidenceServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private EvidenceService evidenceService;

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
}