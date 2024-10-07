package uk.gov.justice.laa.crime.evidence.service;

import java.util.List;
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

    public boolean isRequiredEvidenceOutstanding(List<ApiIncomeEvidence> requestEvidenceItems) {
        if (requestEvidenceItems == null || requestEvidenceItems.isEmpty()) {
            return false;
        }

        List<Integer> incomeEvidenceIds = requestEvidenceItems.stream()
                                                              .filter(item -> item.getDateReceived() == null)
                                                              .map(ApiIncomeEvidence::getId).toList();

        if (incomeEvidenceIds.isEmpty()) {
            return false;
        }

        List<IncomeEvidenceRequiredItemEntity> evidenceItems = incomeEvidenceRepository.findByIds(incomeEvidenceIds);



        return evidenceItems.stream().anyMatch(item -> "Y".equals(item.getMandatory()));
    }


}
