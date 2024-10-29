package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidenceItems;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.evidence.dto.CreateEvidenceDTO;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceReceivedResultDTO;
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRequiredItemRepository;
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRequiredRepository;
import uk.gov.justice.laa.crime.evidence.staticdata.entity.IncomeEvidenceRequiredEntity;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.ApplicantType;
import uk.gov.justice.laa.crime.evidence.staticdata.projection.IncomeEvidenceRequiredItemProjection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeEvidenceService {

    private final IncomeEvidenceRequiredRepository incomeEvidenceRequiredRepository;
    private final IncomeEvidenceRequiredItemRepository incomeEvidenceRequiredItemRepository;

    public boolean checkEvidenceReceived(
        List<ApiIncomeEvidence> evidenceItems,
        MagCourtOutcome magCourtOutcome,
        EmploymentStatus applicantEmploymentStatus,
        EmploymentStatus partnerEmploymentStatus,
        BigDecimal pensionAmount,
        ApplicantType applicantType
    ) {
        EvidenceReceivedResultDTO evidenceReceivedResult = checkMinimumEvidenceItemsReceived(
            evidenceItems,
            applicantType,
            magCourtOutcome,
            applicantEmploymentStatus,
            partnerEmploymentStatus,
            pensionAmount
        );

        if (!evidenceReceivedResult.isEvidenceReceived()) {
            return false;
        }

        boolean requiredEvidenceOutstanding = isRequiredEvidenceOutstanding(
            evidenceReceivedResult.getIncomeEvidenceRequiredId(), evidenceItems);

        return !requiredEvidenceOutstanding;
    }

    public boolean isRequiredEvidenceOutstanding(int incomeEvidenceRequiredId, List<ApiIncomeEvidence> providedEvidenceItems) {
        /*
         Note: The income evidence items passed in are only those items provided. There may be
          many more evidence items required than are passed in, therefore we need to call out to
          find all the required income evidence items first (based on the income evidence
          required id) and then filter down to check that the mandatory items are present.
        */
        List<IncomeEvidenceRequiredItemProjection> requiredEvidenceItems = incomeEvidenceRequiredItemRepository
            .findByIncomeEvidenceRequiredId(incomeEvidenceRequiredId)
            .stream()
            .filter(item -> "Y".equals(item.getMandatory()))
            .toList();

        if (requiredEvidenceItems.isEmpty()) {
            return false;
        }

        if (providedEvidenceItems == null || providedEvidenceItems.isEmpty()) {
            return true;
        }

        for (IncomeEvidenceRequiredItemProjection requiredEvidenceItem : requiredEvidenceItems) {
            Optional<ApiIncomeEvidence> evidenceItem = providedEvidenceItems.stream()
                .filter(providedEvidenceItem -> providedEvidenceItem.getEvidenceType().getName().equals(requiredEvidenceItem.getIncomeEvidenceRequiredDescription()))
                .findFirst();

            if (evidenceItem.isEmpty() || evidenceItem.get().getDateReceived() == null) {
                return true;
            }
        }

        return false;
    }

    public EvidenceReceivedResultDTO checkMinimumEvidenceItemsReceived(
        List<ApiIncomeEvidence> providedEvidenceItems,
        ApplicantType applicantType,
        MagCourtOutcome magCourtOutcome,
        EmploymentStatus applicantEmploymentStatus,
        EmploymentStatus partnerEmploymentStatus,
        BigDecimal pensionAmount) {
        IncomeEvidenceRequiredEntity incomeEvidenceRequiredEntity = incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(
            magCourtOutcome.getOutcome(),
            applicantEmploymentStatus.getCode(),
            partnerEmploymentStatus.getCode(),
            applicantType.toString(),
            pensionAmount.doubleValue());

        if (incomeEvidenceRequiredEntity == null) {
            return new EvidenceReceivedResultDTO(true, 0, 0);
        }

        boolean minimumEvidenceItemsReceived = providedEvidenceItems.size() >= incomeEvidenceRequiredEntity.getEvidenceItemsRequired();

        return new EvidenceReceivedResultDTO(minimumEvidenceItemsReceived, incomeEvidenceRequiredEntity.getId(), incomeEvidenceRequiredEntity.getEvidenceItemsRequired());
    }

    public ApiCreateIncomeEvidenceResponse createEvidence(CreateEvidenceDTO createEvidenceDTO) {

        List<ApiIncomeEvidence> applicantIncomeEvidenceList = getDefaultEvidenceItems(
                createEvidenceDTO, ApplicantType.APPLICANT, createEvidenceDTO.getApplicantPensionAmount());
        ApiCreateIncomeEvidenceResponse apiCreateIncomeEvidenceResponse = new ApiCreateIncomeEvidenceResponse()
                .withApplicantEvidenceItems(new ApiIncomeEvidenceItems()
                        .withApplicantDetails(createEvidenceDTO.getApplicantDetails())
                        .withIncomeEvidenceItems(applicantIncomeEvidenceList));

        if (createEvidenceDTO.getPartnerDetails() != null) {
            List<ApiIncomeEvidence> incomeEvidenceList = getDefaultEvidenceItems(
                    createEvidenceDTO, ApplicantType.PARTNER, createEvidenceDTO.getPartnerPensionAmount());
            apiCreateIncomeEvidenceResponse.withPartnerEvidenceItems(new ApiIncomeEvidenceItems()
                    .withIncomeEvidenceItems(incomeEvidenceList)
                    .withApplicantDetails(createEvidenceDTO.getPartnerDetails()));
        }

        return apiCreateIncomeEvidenceResponse;
    }

    private List<ApiIncomeEvidence> getDefaultEvidenceItems(CreateEvidenceDTO createEvidenceDTO, ApplicantType applicantType, double pensionAmount) {
        IncomeEvidenceRequiredEntity incomeEvidenceRequiredEntity = incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(
                createEvidenceDTO.getMagCourtOutcome().getOutcome(),
                createEvidenceDTO.getApplicantDetails().getEmploymentStatus().getCode(),
                createEvidenceDTO.getPartnerDetails() != null ? createEvidenceDTO.getPartnerDetails().getEmploymentStatus().getCode() : null,
                applicantType.toString(),
                pensionAmount);

        if (incomeEvidenceRequiredEntity != null && incomeEvidenceRequiredEntity.getEvidenceItemsRequired() > 0) {
            return incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(incomeEvidenceRequiredEntity.getId())
                    .stream()
                    .map(this::buildEvidence)
                    .toList();
        }

        return null;
    }

    private ApiIncomeEvidence buildEvidence(IncomeEvidenceRequiredItemProjection incomeEvidenceRequiredItemProjection) {
        return new ApiIncomeEvidence()
                .withMandatory("Y".equals(incomeEvidenceRequiredItemProjection.getMandatory()))
                .withEvidenceType(IncomeEvidenceType.getFrom(incomeEvidenceRequiredItemProjection.getIncomeEvidenceRequiredDescription()));
    }
}
