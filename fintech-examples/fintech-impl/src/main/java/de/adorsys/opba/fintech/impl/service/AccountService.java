package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.service.mocks.TppListAccountsMock;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService extends HandleAcceptedService {
    @Value("${mock.tppais.listaccounts:false}")
    String mockTppAisString;

    private final TppAisClient tppAisClient;

    public ResponseEntity listAccounts(ContextInformation contextInformation, SessionEntity sessionEntity, String bankId, String fintechRedirectURLOK, String fintechRedirectURLNOK) {
        if (mockTppAisString != null && mockTppAisString.equalsIgnoreCase("true") ? true : false) {
            log.warn("Mocking call to list accounts");
            return new ResponseEntity<>(new TppListAccountsMock().getAccountList(), HttpStatus.OK);
        }

        ResponseEntity<AccountList> accounts = tppAisClient.getAccounts(
                contextInformation.getFintechID(),
                contextInformation.getServiceSessionPassword(),
                sessionEntity.getLoginUserName(),
                fintechRedirectURLOK,
                fintechRedirectURLNOK,
                contextInformation.getXRequestID(),
                bankId,
                null,
                null);
        switch (accounts.getStatusCode()) {
            case OK:
                return new ResponseEntity<>(accounts.getBody(), HttpStatus.OK);
            case ACCEPTED:
                return handleAccepted(accounts.getHeaders());
            case UNAUTHORIZED:
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            default:
                throw new RuntimeException("DID NOT EXPECT RETURNCODE:" + accounts.getStatusCode());
        }
    }
}
