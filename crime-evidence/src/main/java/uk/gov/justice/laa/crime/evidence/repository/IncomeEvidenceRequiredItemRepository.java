package uk.gov.justice.laa.crime.evidence.repository;

import uk.gov.justice.laa.crime.evidence.staticdata.entity.IncomeEvidenceRequiredItemEntity;
import uk.gov.justice.laa.crime.evidence.staticdata.projection.IncomeEvidenceRequiredItemProjection;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeEvidenceRequiredItemRepository extends JpaRepository<IncomeEvidenceRequiredItemEntity, Integer> {
    List<IncomeEvidenceRequiredItemProjection> findByIncomeEvidenceRequiredId(Integer id);
}
