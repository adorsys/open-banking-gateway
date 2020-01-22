package de.adorsys.opba.tppbankingapi.services;

import de.adorsys.opba.tppbankingapi.dto.TestResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Getter
@Setter
public class StatisticService {

    private List<TestResult> testResult = Collections.synchronizedList(new ArrayList<>());
}
