package de.adorsys.opba.tppbankingapi.ais.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.protocol.services.ais.account.AccountsReport;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;

@UtilityClass
final class AccountsReportProducer {
    public static AccountsReport produceAccountReport() {
        ObjectMapper mockObjectMapper = new ObjectMapper();

        try (InputStream is = AccountsReportProducer.class.getResourceAsStream("/AccountsReport.json")) {
            return mockObjectMapper.readValue(is, AccountsReport.class);
        } catch (IOException e) {
            return null;
        }
    }
}
