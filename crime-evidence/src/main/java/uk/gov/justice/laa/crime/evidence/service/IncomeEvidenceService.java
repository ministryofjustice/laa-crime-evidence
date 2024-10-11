package uk.gov.justice.laa.crime.evidence.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.enums.EmploymentStatus;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.evidence.dto.EvidenceReceivedResultDTO;
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRequiredItemRepository;
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRequiredRepository;
import uk.gov.justice.laa.crime.evidence.staticdata.entity.IncomeEvidenceRequiredEntity;
import uk.gov.justice.laa.crime.evidence.staticdata.enums.ApplicantType;
import uk.gov.justice.laa.crime.evidence.staticdata.projection.IncomeEvidenceRequiredItemProjection;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeEvidenceService {

    private final IncomeEvidenceRequiredRepository incomeEvidenceRequiredRepository;
    private final IncomeEvidenceRequiredItemRepository incomeEvidenceRequiredItemRepository;

    public boolean isRequiredEvidenceOutstanding(int incomeEvidenceRequiredId, List<ApiIncomeEvidence> providedEvidenceItems) {
        // Note: The income evidence items passed in are only those items provided. There may be
        //  many more evidence items required than are passed in, therefore we need to call out to
        //  find all of the required income evidence items first (based on the income evidence
        //  required id) and then filter down to check that the mandatory items are present.
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

        // TODO: Is the check in here correct (on id to id)?
        for (IncomeEvidenceRequiredItemProjection requiredEvidenceItem : requiredEvidenceItems) {
            Optional<ApiIncomeEvidence> evidenceItem = providedEvidenceItems.stream()
                .filter(providedEvidenceItem -> providedEvidenceItem.getId().equals(requiredEvidenceItem.getId()))
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
            return new EvidenceReceivedResultDTO(true, 0);
        }

        // NOTE: Assumption here from the SP that if we get a null value back from the query to the
        // income evidence required table, then this isn't an error.
        boolean minimumEvidenceItemsReceived = providedEvidenceItems.size() >= incomeEvidenceRequiredEntity.getEvidenceItemsRequired();

        return new EvidenceReceivedResultDTO(minimumEvidenceItemsReceived, incomeEvidenceRequiredEntity.getEvidenceItemsRequired());
    }
}
