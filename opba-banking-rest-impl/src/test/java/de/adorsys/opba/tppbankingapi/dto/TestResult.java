package de.adorsys.opba.tppbankingapi.dto;

import lombok.Value;

@Value
public class TestResult {
    private long start;
    private long end;
    private String searchString;
    private String result;
}
