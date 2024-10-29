package uk.gov.justice.laa.crime.evidence.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.evidence.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.evidence.dto.CreateEvidenceDTO;

import java.math.BigDecimal;
import java.util.stream.Stream;

@ExtendWith(SoftAssertionsExtension.class)
class CreateEvidenceDTOBuilderTest {
    @InjectSoftAssertions
    private SoftAssertions softly;

    public static Stream<Arguments> createIncomeEvidenceRequest() {
        return Stream.of(
            Arguments.of(new ApiCreateIncomeEvidenceRequest()
                .withApplicantDetails(TestModelDataBuilder.getApiApplicantDetails())),
            Arguments.of(new ApiCreateIncomeEvidenceRequest()
                .withApplicantDetails(TestModelDataBuilder.getApiApplicantDetails())
                .withApplicantPensionAmount(BigDecimal.ONE)),
            Arguments.of(new ApiCreateIncomeEvidenceRequest()
                .withApplicantDetails(TestModelDataBuilder.getApiApplicantDetails())
                .withPartnerDetails(TestModelDataBuilder.getApiPartnerDetails())
                .withPartnerPensionAmount(BigDecimal.TEN))
        );
    }

    @ParameterizedTest
    @MethodSource("createIncomeEvidenceRequest")
    void givenCreateIncomeEvidenceRequest_whenBuildIsInvoked_thenCorrectCreateEvidenceDTOFieldsArePopulated(ApiCreateIncomeEvidenceRequest request) {
        double applicantPension = request.getApplicantPensionAmount() != null ? request.getApplicantPensionAmount().doubleValue() : 0;
        double partnerPension = request.getPartnerPensionAmount() != null ? request.getPartnerPensionAmount().doubleValue() : 0;
        CreateEvidenceDTO createEvidenceDTO = CreateEvidenceDTOBuilder.build(request);
        softly.assertThat(createEvidenceDTO.getMagCourtOutcome()).isEqualTo(request.getMagCourtOutcome());
        softly.assertThat(createEvidenceDTO.getPartnerDetails()).isEqualTo(request.getPartnerDetails());
        softly.assertThat(createEvidenceDTO.getApplicantDetails()).isEqualTo(request.getApplicantDetails());

        softly.assertThat(createEvidenceDTO.getPartnerPensionAmount()).isEqualTo(partnerPension);
        softly.assertThat(createEvidenceDTO.getApplicantPensionAmount()).isEqualTo(applicantPension);
        softly.assertAll();
    }
}
