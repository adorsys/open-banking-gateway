package de.adorsys.opba.tppbanking.services.ais.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.tppbanking.services.psuconsentsession.PsuConsentSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetAccountsServiceMockImpl implements GetAccountsService {
    @Value("classpath:mock-ais-get-accounts-response.json")
    private Resource mockAccountsReportFile;

    private final ObjectMapper mockObjectMapper;

    @Override
    public Optional<AccountsReport> getAccountsFor(PsuConsentSession psuConsentSession) {
        try {
            return Optional.ofNullable(mockObjectMapper.readValue(mockAccountsReportFile.getFile(), AccountsReport.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
