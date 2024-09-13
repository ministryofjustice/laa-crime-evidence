package uk.gov.justice.laa.crime.evidence.staticdata.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

public enum OtherEvidenceTypes {
    OTHER("OTHER"),
    OTHER_BUSINESS("OTHER BUSINESS"),
    OTHER_ADHOC("OTHER_ADHOC");

    private final String evidence;

    OtherEvidenceTypes(String evidence) {
        this.evidence = evidence;
    }

    public String getEvidence() {
        return evidence;
    }

    public static OtherEvidenceTypes getFrom(String type) {
        if (StringUtils.isBlank(type)) return null;

        return Stream.of(OtherEvidenceTypes.values())
                .filter(ie -> ie.evidence.equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Evidence Type: %s does not exist.", type)));
    }
}
