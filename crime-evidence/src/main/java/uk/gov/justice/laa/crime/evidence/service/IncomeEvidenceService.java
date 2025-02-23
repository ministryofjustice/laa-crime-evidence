package uk.gov.justice.laa.crime.evidence.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidenceItems;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeEvidenceService {

    private final IncomeEvidenceRequiredRepository incomeEvidenceRequiredRepository;
    private final IncomeEvidenceRequiredItemRepository incomeEvidenceRequiredItemRepository;
    private final IncomeEvidenceValidationService incomeEvidenceValidationService;

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
            partnerEmploymentStatus != null ? partnerEmploymentStatus.getCode() : null,
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

    private List<ApiIncomeEvidence> getDefaultEvidenceItems(CreateEvidenceDTO createEvidenceDTO, ApplicantType applicantType, BigDecimal pensionAmount) {
        IncomeEvidenceRequiredEntity incomeEvidenceRequiredEntity = incomeEvidenceRequiredRepository.getNumberOfEvidenceItemsRequired(
                createEvidenceDTO.getMagCourtOutcome().getOutcome(),
                createEvidenceDTO.getApplicantDetails().getEmploymentStatus().getCode(),
                createEvidenceDTO.getPartnerDetails() != null ? createEvidenceDTO.getPartnerDetails().getEmploymentStatus().getCode() : null,
                applicantType.toString(),
                pensionAmount.doubleValue());

        if (incomeEvidenceRequiredEntity != null && incomeEvidenceRequiredEntity.getEvidenceItemsRequired() > 0) {
            return incomeEvidenceRequiredItemRepository.findByIncomeEvidenceRequiredId(incomeEvidenceRequiredEntity.getId())
                    .stream()
                    .map(this::buildEvidence)
                    .toList();
        }

        return new ArrayList<>();
    }

    private ApiIncomeEvidence buildEvidence(IncomeEvidenceRequiredItemProjection incomeEvidenceRequiredItemProjection) {
        return new ApiIncomeEvidence()
                .withMandatory("Y".equals(incomeEvidenceRequiredItemProjection.getMandatory()))
                .withEvidenceType(IncomeEvidenceType.getFrom(incomeEvidenceRequiredItemProjection.getIncomeEvidenceRequiredDescription()));
    }

    public ApiUpdateIncomeEvidenceResponse updateEvidence(UpdateEvidenceDTO updateEvidenceDTO) {
        List<ApiIncomeEvidence> applicantEvidenceItems = updateEvidenceDTO.getApplicantIncomeEvidenceItems();
        List<ApiIncomeEvidence> partnerEvidenceItems = updateEvidenceDTO.getPartnerIncomeEvidenceItems();

        if (applicantEvidenceItems.isEmpty() && partnerEvidenceItems.isEmpty()) {
            throw new IllegalArgumentException("No income evidence items provided");
        }

        incomeEvidenceValidationService.checkEvidenceReceivedDate(
                DateUtil.parseLocalDate(updateEvidenceDTO.getEvidenceReceivedDate()),
                updateEvidenceDTO.getApplicationReceivedDate());

        incomeEvidenceValidationService.checkExtraEvidenceDescriptions(applicantEvidenceItems);
        incomeEvidenceValidationService.checkExtraEvidenceDescriptions(partnerEvidenceItems);

        incomeEvidenceValidationService.checkEvidenceDueDates(
                DateUtil.parseLocalDate(updateEvidenceDTO.getEvidenceDueDate()),
                DateUtil.parseLocalDate(updateEvidenceDTO.getPreviousEvidenceDueDate()),
                updateEvidenceDTO.isEvidencePending());

        boolean applicantEvidenceItemsReceived = true;
        boolean partnerEvidenceItemsReceived = true;

        if (!applicantEvidenceItems.isEmpty()) {
            applicantEvidenceItemsReceived = checkEvidenceReceived(
                    applicantEvidenceItems,
                    updateEvidenceDTO.getMagCourtOutcome(),
                    updateEvidenceDTO.getApplicantDetails().getEmploymentStatus(),
                    updateEvidenceDTO.getPartnerDetails() != null
                            ? updateEvidenceDTO.getPartnerDetails().getEmploymentStatus() : null,
                    updateEvidenceDTO.getApplicantPensionAmount(),
                    ApplicantType.APPLICANT);
        }

        if (!partnerEvidenceItems.isEmpty()) {
            partnerEvidenceItemsReceived = checkEvidenceReceived(
                    partnerEvidenceItems,
                    updateEvidenceDTO.getMagCourtOutcome(),
                    updateEvidenceDTO.getApplicantDetails().getEmploymentStatus(),
                    updateEvidenceDTO.getPartnerDetails().getEmploymentStatus(),
                    updateEvidenceDTO.getPartnerPensionAmount(),
                    ApplicantType.PARTNER);
        }

        boolean allEvidenceItemsReceived = applicantEvidenceItemsReceived && partnerEvidenceItemsReceived;

        incomeEvidenceValidationService.validateUpliftDates(updateEvidenceDTO, allEvidenceItemsReceived);

        updateEvidenceReceivedDate(updateEvidenceDTO, allEvidenceItemsReceived);
        updateEvidenceDueDate(updateEvidenceDTO);
        updateUpliftRemovedDate(updateEvidenceDTO, allEvidenceItemsReceived);

        ApiUpdateIncomeEvidenceResponse response = new ApiUpdateIncomeEvidenceResponse()
                .withApplicantEvidenceItems(new ApiIncomeEvidenceItems(updateEvidenceDTO.getApplicantDetails(), applicantEvidenceItems))
                .withDueDate(DateUtil.parseLocalDate(updateEvidenceDTO.getEvidenceDueDate()))
                .withAllEvidenceReceivedDate(DateUtil.parseLocalDate(updateEvidenceDTO.getEvidenceReceivedDate()))
                .withUpliftAppliedDate(updateEvidenceDTO.getUpliftAppliedDate())
                .withUpliftRemovedDate(updateEvidenceDTO.getUpliftRemovedDate());

        if (null!= partnerEvidenceItems && !partnerEvidenceItems.isEmpty()) {
            response.setPartnerEvidenceItems(new ApiIncomeEvidenceItems(updateEvidenceDTO.getPartnerDetails(), partnerEvidenceItems));
        }

        return response;
    }

    private void updateUpliftRemovedDate(UpdateEvidenceDTO updateEvidenceDTO, boolean allEvidenceItemsReceived) {
        if (updateEvidenceDTO.getUpliftAppliedDate() != null &&
                !updateEvidenceDTO.getUpliftAppliedDate().equals(updateEvidenceDTO.getOldUpliftAppliedDate())) {
            updateEvidenceDTO.setUpliftRemovedDate(null);
        }

        if (allEvidenceItemsReceived && updateEvidenceDTO.getUpliftAppliedDate() != null
                && updateEvidenceDTO.getUpliftRemovedDate() == null) {
            updateEvidenceDTO.setUpliftRemovedDate(LocalDate.now());
        }
    }

    private void updateEvidenceDueDate(UpdateEvidenceDTO updateEvidenceDTO) {
        LocalDateTime previousEvidenceDueDate = updateEvidenceDTO.getPreviousEvidenceDueDate();

        if (updateEvidenceDTO.getEvidenceDueDate() == null && previousEvidenceDueDate != null) {
            updateEvidenceDTO.setEvidenceDueDate(previousEvidenceDueDate);
        }
    }

    private void updateEvidenceReceivedDate(UpdateEvidenceDTO updateEvidenceDTO, boolean evidenceReceived) {
        if (evidenceReceived && updateEvidenceDTO.getEvidenceReceivedDate() == null) {
            updateEvidenceDTO.setEvidenceReceivedDate(LocalDateTime.now());
        } else if (!evidenceReceived && updateEvidenceDTO.getEvidenceReceivedDate() != null) {
            updateEvidenceDTO.setEvidenceReceivedDate(null);
        }
    }
}
