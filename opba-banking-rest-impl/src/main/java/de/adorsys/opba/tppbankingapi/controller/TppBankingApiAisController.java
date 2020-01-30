package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.ErrorResult;
import de.adorsys.opba.protocol.facade.services.ais.ListAccountsService;
import de.adorsys.opba.protocol.facade.services.ais.ListTransactionsService;
import de.adorsys.opba.tppbankingapi.ais.model.generated.GeneralError;
import de.adorsys.opba.tppbankingapi.ais.resource.generated.TppBankingApiAccountInformationServiceAisApi;
import de.adorsys.opba.tppbankingapi.service.ErrorResultMapper;
import de.adorsys.opba.tppbankingapi.service.FacadeResponseMapper;
import de.adorsys.opba.tppbankingapi.useragent.UserAgentContext;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.tppbankingapi.Const.API_MAPPERS_PACKAGE;
import static de.adorsys.opba.tppbankingapi.Const.SPRING_KEYWORD;

@RestController
@RequiredArgsConstructor
public class TppBankingApiAisController implements TppBankingApiAccountInformationServiceAisApi {

    private final UserAgentContext userAgentContext;
    private final ListAccountsService accounts;
    private final ListTransactionsService transactions;
    private final FacadeResponseMapper mapper;
    private final ErrorResultMapper<ErrorResult, GeneralError> errorMapper;

    @Override
    public CompletableFuture getAccounts(
            String authorization,
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID,
            String bankID,
            String serviceSessionID,
            String validationSessionID,
            String authSessionID
    ) {
        return accounts.list(
                ListAccountsRequest.builder()
                        .uaContext(userAgentContext)
                        .serviceSessionId(serviceSessionID)
                        .validationSessionId(validationSessionID)
                        .authSessionId(authSessionID)
                        .authorization(authorization)
                        .fintechUserID(fintechUserID)
                        .fintechRedirectURLOK(fintechRedirectURLOK)
                        .fintechRedirectURLNOK(fintechRedirectURLNOK)
                        .xRequestID(xRequestID)
                        .bankID(bankID)
                        .build()
        ).thenApply(res -> mapper.translate(res, errorMapper));
    }

    @Override
    public CompletableFuture getTransactions(
            String accountId,
            String authorization,
            String fintechUserID,
            String fintechRedirectURLOK,
            String fintechRedirectURLNOK,
            UUID xRequestID, String bankID,
            String serviceSessionID,
            String validationSessionID,
            String authSessionID,
            LocalDate dateFrom,
            LocalDate dateTo,
            String entryReferenceFrom,
            String bookingStatus,
            Boolean deltaList
    ) {
        return transactions.list(
                ListTransactionsRequest.builder()
                        .uaContext(userAgentContext)
                        .serviceSessionId(serviceSessionID)
                        .validationSessionId(validationSessionID)
                        .authSessionId(authSessionID)
                        .accountId(accountId)
                        .fintechUserID(fintechUserID)
                        .authorization(authorization)
                        .fintechRedirectURLOK(fintechRedirectURLOK)
                        .fintechRedirectURLNOK(fintechRedirectURLNOK)
                        .xRequestID(xRequestID)
                        .bankID(bankID)
                        .dateFrom(dateFrom)
                        .dateTo(dateTo)
                        .entryReferenceFrom(entryReferenceFrom)
                        .bookingStatus(bookingStatus)
                        .deltaList(deltaList)
                        .build()
        ).thenApply(res -> mapper.translate(res, errorMapper));
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = API_MAPPERS_PACKAGE)
    public interface ToErrorResponse extends ErrorResultMapper<ErrorResult, GeneralError> {
        GeneralError map(ErrorResult error);
    }
}
