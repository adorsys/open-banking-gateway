package de.adorsys.opba.fintech.server;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;

class FinTechApiBaseTest {
    private static final String BANK_SEARCH_RESPONSE_PREFIX = "TPP_BankSearchResponse";
    private static final String BANK_PROFILE_RESPONSE_PREFIX = "TPP_BankProfileResponse";
    private static final String POSTFIX = ".json";

    @SneakyThrows
    protected String readFile(String fileName) {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(fileName), StandardCharsets.UTF_8);
    }

    protected String getFilenameBankSearch(String keyword, Integer start, Integer max) {
        return BANK_SEARCH_RESPONSE_PREFIX
                + "-" + keyword
                + "-" + start
                + "-" + max
                + POSTFIX;
    }

    protected String getFilenameBankProfile(String bankUUID) {
        return BANK_PROFILE_RESPONSE_PREFIX
                + "-" + bankUUID
                + POSTFIX;
    }
}
