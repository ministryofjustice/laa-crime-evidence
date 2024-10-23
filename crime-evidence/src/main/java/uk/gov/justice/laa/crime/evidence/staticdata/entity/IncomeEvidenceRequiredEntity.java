package uk.gov.justice.laa.crime.evidence.staticdata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "income_evidence_required", schema = "crime_evidence")
public class IncomeEvidenceRequiredEntity {
    @Id
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "MCOO_OUTCOME", nullable = false)
    private String mcooOutcome;

    @Column(name = "APPLICANT_EMST_CODE", nullable = false)
    private String applicantEmstCode;

    @Column(name = "EVIDENCE_ITEMS_REQUIRED", nullable = false)
    private int evidenceItemsRequired;

    @Column(name = "ANNUAL_PENSION_AMOUNT", nullable = false)
    private int annualPensionAmount;

    @Column(name = "APPLICANT_PARTNER")
    private String applicantPartner;

    @Column(name = "PARTNER_EMST_CODE")
    private String partnerEmstCode;

    @Column(name = "USER_CREATED", nullable = false)
    private String createdBy;

    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @Column(name = "USER_MODIFIED", nullable = false)
    private String modifiedBy;

    @Column(name = "DATE_MODIFIED")
    @UpdateTimestamp
    private LocalDateTime modifiedDateTime;
}
