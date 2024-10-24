package uk.gov.justice.laa.crime.evidence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.evidence.staticdata.entity.IncomeEvidenceRequiredEntity;

@Repository
public interface IncomeEvidenceRequiredRepository extends CrudRepository<IncomeEvidenceRequiredEntity, String> {
    @Query(
        value =
            """
                select ID,
                       EVIDENCE_ITEMS_REQUIRED
                from   income_evidence_required i
                where  mcoo_outcome = ?1
                and    applicant_EMST_CODE  = ?2
                and    ((partner_emst_code is null and ?3 is null) or (partner_emst_code = ?3))
                and    I.APPLICANT_PARTNER = ?4
                and    I.ANNUAL_PENSION_AMOUNT <= nvl(?5,0)
                and    not exists (select 1
                                    from income_evidence_required i2
                                    where i2.mcoo_outcome            = ?1
                                      and i2.applicant_EMST_CODE     = ?2
                                      and ((i2.partner_emst_code is null and ?3 is null) or (i2.partner_emst_code = ?3))
                                      and  I2.APPLICANT_PARTNER      = ?4
                                      and  i2.ANNUAL_PENSION_AMOUNT <= nvl(?5,0)
                                      and i2.ANNUAL_PENSION_AMOUNT   > I.ANNUAL_PENSION_AMOUNT)
            """,
        nativeQuery = true)
    IncomeEvidenceRequiredEntity getNumberOfEvidenceItemsRequired(String mcooOutcome, String applicantEmstCode, String partnerEmstCode, String applicantPartner, Double annualPensionAmount);
}
