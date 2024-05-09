package uk.gov.justice.laa.crime.evidence.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

/**
 * static data migrated from TOGDATA.EVIDENCE_FEE_RULES table
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum IncomeEvidence {

    SIGNATURE("SIGNATURE", "Signature", "Signature", "Llofnod", "N"),
    CDS15("CDS15", "CDS 15", "CDS 15", "CDS 15", "Y"),
    FREEZING("FREEZING", "Freezing order", "Freezing order", "Gorchymyn Rhewi", "Y"),
    RESTRAINING("RESTRAINING", "Restraint Order", "Restraint Order", "Gorchymyn Atal", "Y"),
    CONFISCATION("CONFISCATION", "Confiscation order", "Confiscation order", "Gorchymyn Atafaeliad", "Y"),
    OTHER_ADHOC("OTHER_ADHOC", "Other Adhoc", "Other Adhoc", "Ad Hoc eraill", "Y"),
    EMP_LETTER_ADHOC("EMP LETTER ADHOC", "Letter from Employer", "Letter from Employer", "Llythyr o'r cyflogwr", "Y"),
    WAGE_SLIP_ADHOC("WAGE SLIP ADHOC", "Wage Slip", "Wage Slip within past 3 months", "Papur Cyflog o fewn y tri mis diwethaf", "Y"),
    NINO("NINO", "National Insurance Number", "National Insurance Number", "Rhif Yswiriant Cenedlaethol", "N"),
    ACCOUNTS("ACCOUNTS", "Set of Accounts", "Set of Accounts", "Cyfrifon", "N"),
    OTHER_BUSINESS("OTHER BUSINESS", "Other Business Records", "Other Business Records", "Cofnodion Busnes eraill", "N"),
    CASH_BOOK("CASH BOOK", "Cash Book", "Cash Book", "Llyfr Arian", "N"),
    WAGE_SLIP("WAGE SLIP", "Wage Slip", "Wage Slip within past 3 months", "Papur Cyflog o fewn y tri mis diwethaf", "N"),
    BANK_STATEMENT("BANK STATEMENT", "Bank Statement", "Bank Statement(s) covering 3 months", "Cyfriflen Banc", "N"),
    TAX_RETURN("TAX RETURN", "Tax Return", "Tax Return", "Ffurflen Dreth", "N"),
    EMP_LETTER("EMP LETTER", "Letter from Employer", "Letter from Employer", "Llythyr oddi wrth Gyflogwr", "N"),
    OTHER("OTHER", "Other Ad-hoc evidence", "Text to be entered", "", "N");

    @JsonPropertyDescription("Specifies the Income Evidence")
    private final String evidence;
    private final String description;
    private final String letterDescription;
    private final String welshLetterDescription;
    private final String adhoc;

    public static IncomeEvidence getFrom(String type) {
        if (StringUtils.isBlank(type)) return null;

        return Stream.of(IncomeEvidence.values())
                .filter(ie -> ie.evidence.equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Income evidence : %s does not exist.", type)));
    }
}