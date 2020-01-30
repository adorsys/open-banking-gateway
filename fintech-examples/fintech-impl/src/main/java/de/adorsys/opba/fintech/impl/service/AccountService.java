package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2003;
import de.adorsys.opba.fintech.impl.config.FinTechImplConfig;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import de.adorsys.opba.fintech.impl.service.mapper.Mapper;
import de.adorsys.opba.fintech.impl.service.mocks.TppListAccountsMock;
import de.adorsys.opba.tpp.ais.api.model.generated.AccountList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final FinTechImplConfig.TppAisClient tppAisClient;

    public InlineResponse2003 listAccounts(ContextInformation contextInformation, UserEntity userEntity, String bankId) {

        /*
        ResponseEntity<AccountList>
        getAccounts(
                @ApiParam(value = ""@RequestHeader(value = "Authorization", required = true) String authorization,
                @ApiParam(value = ""@RequestHeader(value = "Fintech-User-ID", required = true) String fintechUserID,
                @ApiParam(value = ""@RequestHeader(value = "Fintech-Redirect-URL-OK", required = true) String fintechRedirectURLOK,
                @ApiParam(value = ""@RequestHeader(value = "Fintech-Redirect-URL-NOK", required = true) String fintechRedirectURLNOK,
                @ApiParam(value = ""@RequestHeader(value = "X-Request-ID", required = true) UUID xRequestID,
                @ApiParam(value = ""@RequestHeader(value = "Bank-ID", required = false) String bankID
        @ApiParam(value = ""@RequestHeader(value = "PSU-Consent-Session", required = false) String psUConsentSession);
         */
        /*
        ResponseEntity<de.adorsys.opba.tpp.ais.api.model.generated.AccountList> accounts = tppAisClient.getAccounts(
                contextInformation.getFintechID(),
                userEntity.getName(),
                "okUrl",
                "notOkUrl",
                contextInformation.getXRequestID(),
                bankId,
                null);
         */
        AccountList accountList = new TppListAccountsMock().getAccountList();
        InlineResponse2003 response = new InlineResponse2003();
        response.setAccountList(Mapper.fromTppToFintech(accountList));
        return response;
    }
}
