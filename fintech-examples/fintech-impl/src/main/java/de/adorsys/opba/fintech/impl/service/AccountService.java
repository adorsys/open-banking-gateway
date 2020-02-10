package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2003;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.mapper.ManualMapper;
import de.adorsys.opba.fintech.impl.service.mocks.TppListAccountsMock;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;

import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.AUTHORIZATION_SESSION_ID;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.PSU_CONSENT_SESSION;
import static de.adorsys.opba.fintech.impl.tppclients.HeaderFields.REDIRECT_CODE;
import static org.springframework.http.HttpStatus.SEE_OTHER;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService extends HandleAcceptedService {
    @Value("${mock.tppais.listaccounts}")
    String mockTppAisString;

    private final TppAisClient tppAisClient;

    public ResponseEntity listAccounts(ContextInformation contextInformation, SessionEntity sessionEntity, String bankId, String fintechRedirectURLOK, String fintechRedirectURLNOK) {
        if (mockTppAisString != null && mockTppAisString.equalsIgnoreCase("true") ? true : false) {
            log.warn("Mocking call to list accounts");
            return createREsponse(new TppListAccountsMock().getAccountList());
        }
        ResponseEntity<AccountList> accounts = tppAisClient.getAccounts(
                contextInformation.getFintechID(),
                sessionEntity.getLoginUserName(),
                fintechRedirectURLOK,
                fintechRedirectURLNOK,
                contextInformation.getXRequestID(),
                bankId,
                null);
        switch (accounts.getStatusCode()) {
            case OK:
                return createREsponse(accounts.getBody());
            case ACCEPTED:
                return handleAccepted(accounts.getHeaders());
            case UNAUTHORIZED:
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            default:
                throw new RuntimeException("DID NOT EXPECT RETURNCODE:" + accounts.getStatusCode());
        }
    }

    private ResponseEntity createREsponse(AccountList accountList) {
        InlineResponse2003 response = new InlineResponse2003();
        response.setAccountList(ManualMapper.fromTppToFintech(accountList));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
