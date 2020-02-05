package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2003;
import de.adorsys.opba.fintech.impl.config.TppAisClient;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.mapper.ManualMapper;
import de.adorsys.opba.fintech.impl.service.mocks.TppListAccountsMock;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    @Value("${mock.tppais.listaccounts}")
    String mockTppAisString;

    private final TppAisClient tppAisClient;

    public InlineResponse2003 listAccounts(ContextInformation contextInformation, SessionEntity sessionEntity, String bankId, String fintechRedirectURLOK, String fintechRedirectURLNOK) {
        if (mockTppAisString != null && mockTppAisString.equalsIgnoreCase("true") ? true : false) {
            log.warn("Mocking call to list accounts");
            return createInlineResponse2003(new TppListAccountsMock().getAccountList());
        }

        try {
            return createInlineResponse2003(tppAisClient.getAccounts(
                    contextInformation.getFintechID(),
                    sessionEntity.getLoginUserName(),
                    fintechRedirectURLOK,
                    fintechRedirectURLNOK,
                    contextInformation.getXRequestID(),
                    bankId,
                    null).getBody());
        } catch (FeignException ex) {
            log.error("got exception for status code " + ex.status());
            throw ex;
        }
    }

    private InlineResponse2003 createInlineResponse2003(AccountList accountList) {
        InlineResponse2003 response = new InlineResponse2003();
        response.setAccountList(ManualMapper.fromTppToFintech(accountList));
        return response;
    }
}
