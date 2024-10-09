package uk.gov.justice.laa.crime.evidence.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import uk.gov.justice.laa.crime.evidence.staticdata.entity.IncomeEvidenceRequiredItemEntity;

public interface IncomeEvidenceRepository extends CrudRepository<IncomeEvidenceRequiredItemEntity, Integer> {
    List<IncomeEvidenceRequiredItemEntity> findByIncomeEvidenceRequiredId(Integer id);


}
