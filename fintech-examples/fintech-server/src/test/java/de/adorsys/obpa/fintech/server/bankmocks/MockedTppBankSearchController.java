package de.adorsys.obpa.fintech.server.bankmocks;

import de.adorsys.opba.tpp.bankserach.api.model.BankSearchResponse;
import de.adorsys.opba.tpp.bankserach.api.resource.TppBankSearchApi;
import de.adorsys.opba.tpp.bankserach.client.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@SuppressWarnings("checkstyle:ParameterNumber")
public class MockedTppBankSearchController extends TppBankSearchApi {
    public MockedTppBankSearchController() {
        log.info("#################################################");
    }
    @Override
    public BankSearchResponse bankSearchGET(String authorization, UUID xRequestID, String keyword, Integer start, Integer max) {
        log.info("I WAS HERE :-");
        return null;
    }
}

