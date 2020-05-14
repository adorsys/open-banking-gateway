package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.properties.CookieConfigProperties;
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

import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_REQUEST_SIGNATURE;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_X_TIMESTAMP_UTC;
import static de.adorsys.opba.fintech.impl.tppclients.Consts.COMPUTE_FINTECH_ID;

@Service
@Slf4j
public class AccountService extends HandleAcceptedService {
    @Value("${mock.tppais.listaccounts:false}")
    String mockTppAisString;

    private final FintechUiConfig uiConfig;
    private final TppAisClient tppAisClient;
    private RestRequestContext restRequestContext;

    @Autowired
    private TppProperties tppProperties;

    @Autowired
    private RedirectHandlerService redirectHandlerService;


    public AccountService(AuthorizeService authorizeService, TppAisClient tppAisClient, FintechUiConfig uiConfig,
                          CookieConfigProperties cookieConfigProperties, RestRequestContext restRequestContext) {
        super(authorizeService, cookieConfigProperties, restRequestContext);
        this.tppAisClient = tppAisClient;
        this.uiConfig = uiConfig;
        this.restRequestContext = restRequestContext;
    }

    public ResponseEntity listAccounts(SessionEntity sessionEntity,
                                       String fintechOkUrl, String fintechNOKUrl,
                                       String bankID) {
        if (mockTppAisString != null && mockTppAisString.equalsIgnoreCase("true") ? true : false) {
            log.warn("Mocking call to list accounts");
            return new ResponseEntity<>(new TppListAccountsMock().getAccountList(), HttpStatus.OK);
        }

        final String fintechRedirectCode = UUID.randomUUID().toString();
        ResponseEntity accounts = readOpbaResponse(bankID, sessionEntity, fintechRedirectCode);

        switch (accounts.getStatusCode()) {
            case OK:
                return new ResponseEntity<>(accounts.getBody(), HttpStatus.OK);
            case ACCEPTED:
                log.info("create redirect entity for redirect code {}", fintechRedirectCode);
                redirectHandlerService.registerRedirectStateForSession(fintechRedirectCode, fintechOkUrl, fintechNOKUrl);
                return handleAccepted(fintechRedirectCode, sessionEntity, accounts.getHeaders());
            case UNAUTHORIZED:
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            default:
                throw new RuntimeException("DID NOT EXPECT RETURNCODE:" + accounts.getStatusCode());
        }
    }

    private ResponseEntity readOpbaResponse(String bankID, SessionEntity sessionEntity, String redirectCode) {
        UUID xRequestId = UUID.fromString(restRequestContext.getRequestId());
        ResponseEntity accounts;
        if (null != sessionEntity.getServiceSessionId() && sessionEntity.getConsentConfirmed()) {
            accounts = tppAisClient.getAccounts(
                    tppProperties.getServiceSessionPassword(),
                    sessionEntity.getUserEntity().getLoginUserName(),
                    RedirectUrlsEntity.buildOkUrl(uiConfig, redirectCode),
                    RedirectUrlsEntity.buildNokUrl(uiConfig, redirectCode),
                    xRequestId,
                    COMPUTE_X_TIMESTAMP_UTC,
                    OperationType.AIS.toString(),
                    COMPUTE_X_REQUEST_SIGNATURE,
                    COMPUTE_FINTECH_ID,
                    bankID,
                    null,
                    sessionEntity.getConsentConfirmed() ? sessionEntity.getServiceSessionId() : null);
        } else {
            // FIXME: HACKETTY-HACK - force consent retrieval for transactions on ALL accounts
            // Should be superseded and fixed with
            // https://github.com/adorsys/open-banking-gateway/issues/303
            accounts = tppAisClient.getTransactions(
                    UUID.randomUUID().toString(), // As consent is missing this will be ignored
                    tppProperties.getServiceSessionPassword(),
                    sessionEntity.getUserEntity().getLoginUserName(),
                    RedirectUrlsEntity.buildOkUrl(uiConfig, redirectCode),
                    RedirectUrlsEntity.buildNokUrl(uiConfig, redirectCode),
                    xRequestId,
                    COMPUTE_X_TIMESTAMP_UTC,
                    OperationType.AIS.toString(),
                    COMPUTE_X_REQUEST_SIGNATURE,
                    COMPUTE_FINTECH_ID,
                    bankID,
                    null,
                    sessionEntity.getConsentConfirmed() ? sessionEntity.getServiceSessionId() : null,
                    null, null, null, null, null);
        }
        return accounts;
    }
}
