package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.ais.ListAccountsService;
import de.adorsys.opba.protocol.facade.services.ais.ListTransactionsService;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import de.adorsys.opba.tppbankingapi.Const;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionsResponse;
import de.adorsys.opba.tppbankingapi.ais.resource.generated.TppBankingApiAccountInformationServiceAisApi;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.tppbankingapi.Const.SPRING_KEYWORD;

@RestController
@RequiredArgsConstructor
public class TppBankingApiAisController implements TppBankingApiAccountInformationServiceAisApi {

    private final UserAgentContext userAgentContext;
    private final ListAccountsService accounts;
    private final ListTransactionsService transactions;
    private final FacadeResponseMapper mapper;
    private final AccountListFacadeResponseBodyToRestBodyMapper accountListRestMapper;
    private final TransactionsFacadeResponseBodyToRestBodyMapper transactionsRestMapper;

    @Override
    public CompletableFuture getAccounts(
            String serviceSessionPassword,
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String xTimestampUTC,
            String xRequestSignature,
            String fintechId,
            String bankID,
            String psUConsentSession,
            UUID serviceSessionId
    ) {
        return accounts.execute(
                ListAccountsRequest.builder()
                        .facadeServiceable(FacadeServiceableRequest.builder()
                                // Get rid of CGILIB here by copying:
                                .uaContext(userAgentContext.toBuilder().build())
                                .authorization(fintechId)
                                .sessionPassword(serviceSessionPassword)
                                .fintechUserId(fintechUserID)
                                .fintechRedirectUrlOk(fintechRedirectURLOK)
                                .fintechRedirectUrlNok(fintechRedirectURLNOK)
                                .serviceSessionId(serviceSessionId)
                                .requestId(xRequestID)
                                .bankId(bankID)
                                .build()
                        ).build()
        ).thenApply((FacadeResult<AccountListBody> result) -> mapper.translate(result, accountListRestMapper));
    }

    @Override
    public CompletableFuture getTransactions(
            String accountId,
            String serviceSessionPassword,
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String xTimestampUTC,
            String xRequestSignature,
            String fintechId,
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
                                .authorization(fintechId)
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
        ).thenApply((FacadeResult<TransactionsResponseBody> result) -> mapper.translate(result, transactionsRestMapper));
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE)
    public interface AccountListFacadeResponseBodyToRestBodyMapper extends FacadeResponseBodyToRestBodyMapper<AccountList, AccountListBody> {
        AccountList map(AccountListBody facadeEntity);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE)
    public interface TransactionsFacadeResponseBodyToRestBodyMapper extends FacadeResponseBodyToRestBodyMapper<TransactionsResponse, TransactionsResponseBody> {
        TransactionsResponse map(TransactionsResponseBody facadeEntity);
    }
}
