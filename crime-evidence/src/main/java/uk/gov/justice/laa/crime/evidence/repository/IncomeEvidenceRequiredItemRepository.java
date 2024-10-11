package uk.gov.justice.laa.crime.evidence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import uk.gov.justice.laa.crime.evidence.staticdata.entity.IncomeEvidenceRequiredItemEntity;
import uk.gov.justice.laa.crime.evidence.staticdata.projection.IncomeEvidenceRequiredItemProjection;

public interface IncomeEvidenceRequiredItemRepository extends JpaRepository<IncomeEvidenceRequiredItemEntity, Integer> {
    List<IncomeEvidenceRequiredItemProjection> findByIncomeEvidenceRequiredId(Integer id);
}
