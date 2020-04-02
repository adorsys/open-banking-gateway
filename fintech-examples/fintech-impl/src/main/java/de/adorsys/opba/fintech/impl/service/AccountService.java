package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.RequestInfoEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.properties.TppProperties;
import de.adorsys.opba.fintech.impl.service.mocks.TppListAccountsMock;
import de.adorsys.opba.fintech.impl.tppclients.TppAisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class AccountService extends HandleAcceptedService {
    @Value("${mock.tppais.listaccounts:false}")
    String mockTppAisString;

    private final FintechUiConfig uiConfig;
    private final TppAisClient tppAisClient;

    @Autowired
    private RestRequestContext restRequestContext;

    @Autowired
    private TppProperties tppProperties;

    public AccountService(AuthorizeService authorizeService, TppAisClient tppAisClient, FintechUiConfig uiConfig) {
        super(authorizeService);
        this.tppAisClient = tppAisClient;
        this.uiConfig = uiConfig;
    }

    public ResponseEntity listAccounts(SessionEntity sessionEntity,
                                       RedirectUrlsEntity redirectUrlsEntity,
                                       RequestInfoEntity requestInfoEntity) {
        if (mockTppAisString != null && mockTppAisString.equalsIgnoreCase("true") ? true : false) {
            log.warn("Mocking call to list accounts");
            return new ResponseEntity<>(new TppListAccountsMock().getAccountList(), HttpStatus.OK);
        }

        ResponseEntity accounts = readOpbaResponse(sessionEntity, redirectUrlsEntity, requestInfoEntity);

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

    private ResponseEntity readOpbaResponse(SessionEntity sessionEntity, RedirectUrlsEntity redirectUrlsEntity, RequestInfoEntity requestInfoEntity) {
        ResponseEntity accounts;
        if (null != sessionEntity.getServiceSessionId()) {
            accounts = tppAisClient.getAccounts(
                    tppProperties.getFintechID(),
                    tppProperties.getServiceSessionPassword(),
                    sessionEntity.getLoginUserName(),
                    redirectUrlsEntity.buildOkUrl(uiConfig),
                    redirectUrlsEntity.buildNokUrl(uiConfig),
                    UUID.fromString(restRequestContext.getRequestId()),
                    requestInfoEntity.getBankId(),
                    sessionEntity.getPsuConsentSession(),
                    sessionEntity.getServiceSessionId());
        } else {
            // FIXME: HACKETTY-HACK - force consent retrieval for transactions on ALL accounts
            // Should be superseded and fixed with
            // https://github.com/adorsys/open-banking-gateway/issues/303
            accounts = tppAisClient.getTransactions(
                    UUID.randomUUID().toString(), // As consent is missing this will be ignored
                    tppProperties.getFintechID(),
                    tppProperties.getServiceSessionPassword(),
                    sessionEntity.getLoginUserName(),
                    redirectUrlsEntity.buildOkUrl(uiConfig),
                    redirectUrlsEntity.buildNokUrl(uiConfig),
                    UUID.fromString(restRequestContext.getRequestId()),
                    requestInfoEntity.getBankId(),
                    sessionEntity.getPsuConsentSession(),
                    sessionEntity.getServiceSessionId(),
                    requestInfoEntity.getDateFrom(),
                    requestInfoEntity.getDateTo(),
                    requestInfoEntity.getEntryReferenceFrom(),
                    requestInfoEntity.getBookingStatus(),
                    requestInfoEntity.getDeltaList());
        }
        return accounts;
    }
}
