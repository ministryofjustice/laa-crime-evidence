package uk.gov.justice.laa.crime.evidence.staticdata.projection;

// TODO: Should this also contain the evidence received date? This doesn't exist on said table,
//  so not sure exactly where this should come from if so.
public interface IncomeEvidenceRequiredItemProjection {
    int getId();
    int getIncomeEvidenceRequiredId();
    String getMandatory();
}
