package uk.gov.justice.laa.crime.evidence.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeEvidenceService {
    
    public boolean isRequiredEvidenceOutstanding(List<ApiIncomeEvidence> evidenceItems) {
        if (evidenceItems == null || evidenceItems.isEmpty()) {
            return false;
        }

        return evidenceItems.stream().anyMatch(item -> Boolean.TRUE.equals(item.getMandatory()) && item.getDateReceived() == null);
    }
}
