package uk.gov.justice.laa.crime.evidence.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.evidence.repository.IncomeEvidenceRepository;
import uk.gov.justice.laa.crime.evidence.staticdata.entity.IncomeEvidenceRequiredItemEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeEvidenceService {

    private final IncomeEvidenceRepository incomeEvidenceRepository;

    public boolean isRequiredEvidenceOutstanding(int incomeEvidenceRequiredId, List<ApiIncomeEvidence> providedEvidenceItems) {
        // Note: The income evidence items passed in are only those items provided. There may be
        //  many more evidence items required than are passed in, therefore we need to call out to
        //  find all of the required income evidence items first (based on the income evidence
        //  required id) and then filter down to check that the mandatory items are present.
        List<IncomeEvidenceRequiredItemEntity> requiredEvidenceItems = incomeEvidenceRepository
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

        for (IncomeEvidenceRequiredItemEntity requiredEvidenceItem : requiredEvidenceItems) {
            Optional<ApiIncomeEvidence> evidenceItem = providedEvidenceItems.stream()
                .filter(providedEvidenceItem -> providedEvidenceItem.getId().equals(requiredEvidenceItem.getId()))
                .findFirst();

            if (evidenceItem.isEmpty() || evidenceItem.get().getDateReceived() == null) {
                return true;
            }
        }

        return false;
    }
}
