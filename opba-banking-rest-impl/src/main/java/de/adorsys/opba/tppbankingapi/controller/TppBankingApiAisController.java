package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.facade.services.ais.ListAccountsService;
import de.adorsys.opba.protocol.facade.services.ais.ListTransactionsService;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import de.adorsys.opba.tppbankingapi.ais.resource.generated.TppBankingApiAccountInformationServiceAisApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class TppBankingApiAisController implements TppBankingApiAccountInformationServiceAisApi {

    private final UserAgentContext userAgentContext;
    private final ListAccountsService accounts;
    private final ListTransactionsService transactions;
    private final FacadeResponseMapper mapper;

    @Override
    public CompletableFuture getAccounts(
            String authorization,
            String serviceSessionPassword,
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String bankID,
            String psUConsentSession,
            UUID serviceSessionId
    ) {
        return accounts.execute(
                ListAccountsRequest.builder()
                        .facadeServiceable(FacadeServiceableRequest.builder()
                                // Get rid of CGILIB here by copying:
                                .uaContext(userAgentContext.toBuilder().build())
                                .authorization(authorization)
                                .sessionPassword(serviceSessionPassword)
                                .fintechUserId(fintechUserID)
                                .fintechRedirectUrlOk(fintechRedirectURLOK)
                                .fintechRedirectUrlNok(fintechRedirectURLNOK)
                                .serviceSessionId(serviceSessionId)
                                .requestId(xRequestID)
                                .bankId(bankID)
                                .build()
                        ).build()
        ).thenApply(mapper::translate);
    }

    @Override
    public CompletableFuture getTransactions(
            String accountId,
            String authorization,
            String serviceSessionPassword,
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String bankID,
            String psUConsentSession,
            UUID serviceSessionId,
            LocalDate dateFrom,
            LocalDate dateTo,
            String entryReferenceFrom,
            String bookingStatus,
            Boolean deltaList
    ) {
        return transactions.execute(
                ListTransactionsRequest.builder()
                        .facadeServiceable(FacadeServiceableRequest.builder()
                                // Get rid of CGILIB here by copying:
                                .uaContext(userAgentContext.toBuilder().build())
                                .authorization(authorization)
                                .sessionPassword(serviceSessionPassword)
                                .fintechUserId(fintechUserID)
                                .fintechRedirectUrlOk(fintechRedirectURLOK)
                                .fintechRedirectUrlNok(fintechRedirectURLNOK)
                                .serviceSessionId(serviceSessionId)
                                .requestId(xRequestID)
                                .bankId(bankID)
                                .build()
                        )
                        .accountId(accountId)
                        .dateFrom(dateFrom)
                        .dateTo(dateTo)
                        .entryReferenceFrom(entryReferenceFrom)
                        .bookingStatus(bookingStatus)
                        .deltaList(deltaList)
                        .build()
        ).thenApply(mapper::translate);
    }
}
