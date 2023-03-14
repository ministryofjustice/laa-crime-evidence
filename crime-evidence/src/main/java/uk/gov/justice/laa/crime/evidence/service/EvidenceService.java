package uk.gov.justice.laa.crime.evidence.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvidenceService {

    private final MaatCourtDataService maatCourtDataService;

    public Integer calculateEvidenceFee(Integer repId, String laaTransactionId) {
        return maatCourtDataService.getRepOrderCapitalByRepId(repId, laaTransactionId);
    }

}
