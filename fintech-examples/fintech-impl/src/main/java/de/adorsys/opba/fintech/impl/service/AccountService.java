package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.RequestInfoEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.service.mocks.TppListAccountsMock;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountService extends HandleAcceptedService {
    @Value("${mock.tppais.listaccounts:false}")
    String mockTppAisString;

    private final FintechUiConfig uiConfig;
    private final TppAisClient tppAisClient;

    public AccountService(AuthorizeService authorizeService, TppAisClient tppAisClient, FintechUiConfig uiConfig) {
        super(authorizeService);
        this.tppAisClient = tppAisClient;
        this.uiConfig = uiConfig;
    }

    public ResponseEntity listAccounts(ContextInformation contextInformation,
                                       SessionEntity sessionEntity,
                                       RedirectUrlsEntity redirectUrlsEntity,
                                       RequestInfoEntity requestInfoEntity) {
        if (mockTppAisString != null && mockTppAisString.equalsIgnoreCase("true") ? true : false) {
            log.warn("Mocking call to list accounts");
            return new ResponseEntity<>(new TppListAccountsMock().getAccountList(), HttpStatus.OK);
        }

        ResponseEntity<AccountList> accounts = tppAisClient.getAccounts(
                contextInformation.getFintechID(),
                contextInformation.getServiceSessionPassword(),
                sessionEntity.getLoginUserName(),
                redirectUrlsEntity.buildOkUrl(uiConfig),
                redirectUrlsEntity.buildNokUrl(uiConfig),
                contextInformation.getXRequestID(),
                requestInfoEntity.getBankId(),
                sessionEntity.getPsuConsentSession(),
                sessionEntity.getServiceSessionId());

        switch (accounts.getStatusCode()) {
            case OK:
                return new ResponseEntity<>(accounts.getBody(), HttpStatus.OK);
            case ACCEPTED:
                return handleAccepted(sessionEntity, accounts.getHeaders());
            case UNAUTHORIZED:
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            default:
                throw new RuntimeException("DID NOT EXPECT RETURNCODE:" + accounts.getStatusCode());
        }
    }
}
