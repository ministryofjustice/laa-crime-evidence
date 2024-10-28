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
@Table(name = "income_evidence_req_items", schema = "crime_evidence")
public class IncomeEvidenceRequiredItemEntity {
    @Id
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "IEVR_ID", nullable = false)
    private int incomeEvidenceRequiredId;

    @Column(name = "INEV_EVIDENCE", nullable = false)
    private String incomeEvidenceRequiredDescription;

    @Column(name = "MANDATORY", nullable = false)
    private String mandatory;

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
