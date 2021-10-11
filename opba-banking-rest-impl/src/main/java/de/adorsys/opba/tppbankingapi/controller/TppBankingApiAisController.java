package de.adorsys.opba.tppbankingapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.protocol.api.dto.context.UserAgentContext;
import de.adorsys.opba.protocol.api.dto.parameters.ExtraRequestParam;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.AisAuthorizationStatusRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.request.accounts.UpdateExternalAisSessionRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.AisConsent;
import de.adorsys.opba.protocol.api.dto.request.authorization.DeleteConsentRequest;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.AisAuthorizationStatusBody;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateExternalAisSessionBody;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.ais.DeleteConsentService;
import de.adorsys.opba.protocol.facade.services.ais.GetAisAuthorizationStatusService;
import de.adorsys.opba.protocol.facade.services.ais.ListAccountsService;
import de.adorsys.opba.protocol.facade.services.ais.ListTransactionsService;
import de.adorsys.opba.protocol.facade.services.ais.UpdateExternalAisSessionService;
import de.adorsys.opba.restapi.shared.GlobalConst;
import de.adorsys.opba.restapi.shared.mapper.FacadeResponseBodyToRestBodyMapper;
import de.adorsys.opba.restapi.shared.service.FacadeResponseMapper;
import de.adorsys.opba.tppbankingapi.Const;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import de.adorsys.opba.tppbankingapi.ais.model.generated.SessionStatusDetails;
import de.adorsys.opba.tppbankingapi.ais.model.generated.TransactionsResponse;
import de.adorsys.opba.tppbankingapi.ais.model.generated.UpdateAisExternalSessionStatus;
import de.adorsys.opba.tppbankingapi.ais.resource.generated.TppBankingApiAccountInformationServiceAisApi;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.tppbankingapi.Const.SPRING_KEYWORD;

@RestController
@RequiredArgsConstructor
public class TppBankingApiAisController implements TppBankingApiAccountInformationServiceAisApi {

    private final UserAgentContext userAgentContext;
    private final DeleteConsentService deleteConsent;
    private final ListAccountsService accounts;
    private final ListTransactionsService transactions;
    private final GetAisAuthorizationStatusService aisSessionStatus;
    private final UpdateExternalAisSessionService updateExternalAis;
    private final FacadeResponseMapper mapper;
    private final AccountListFacadeResponseBodyToRestBodyMapper accountListRestMapper;
    private final TransactionsFacadeResponseBodyToRestBodyMapper transactionsRestMapper;
    private final ConsentAuthorizationSessionStatusToApiMapper sessionStatusToApiMapper;
    private final UpdateExternalAisSessionToApiMapper updateExternalAisSessionToApiMapper;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public CompletableFuture getAccounts(
        String serviceSessionPassword,
        String fintechUserID,
        String fintechRedirectURLOK,
        String fintechRedirectURLNOK,
        UUID xRequestID,
        String xTimestampUTC,
        String xRequestSignature,
        String fintechId,
        UUID bankProfileID,
        Boolean xPsuAuthenticationRequired,
        UUID serviceSessionId,
        String createConsentIfNone,
        String importUserData,
        Boolean computePsuIpAddress,
        String psuIpAddress,
        Boolean withBalance,
        Boolean online
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
                    .bankProfileId(bankProfileID)
                    .anonymousPsu(null != xPsuAuthenticationRequired && !xPsuAuthenticationRequired)
                    .online(online)
                    .build()
                )
                .withBalance(withBalance)
                .extras(getExtras(createConsentIfNone, importUserData))
                .build()
        ).thenApply((FacadeResult<AccountListBody> result) -> mapper.translate(result, accountListRestMapper));
    }

    @Override
    @SneakyThrows
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
        UUID bankProfileID,
        Boolean xPsuAuthenticationRequired,
        UUID serviceSessionId,
        String createConsentIfNone,
        String importUserData,
        Boolean computePsuIpAddress,
        String psuIpAddress,
        LocalDate dateFrom,
        LocalDate dateTo,
        String entryReferenceFrom,
        String bookingStatus,
        Boolean deltaList,
        Boolean online,
        Boolean analytics,
        Integer page,
        Integer pageSize
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
                    .bankProfileId(bankProfileID)
                    .anonymousPsu(null != xPsuAuthenticationRequired && !xPsuAuthenticationRequired)
                    .online(online)
                    .withAnalytics(analytics)
                    .build()
                )
                .accountId(accountId)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .entryReferenceFrom(entryReferenceFrom)
                .bookingStatus(bookingStatus)
                .deltaList(deltaList)
                .page(page)
                .pageSize(pageSize)
                .extras(getExtras(createConsentIfNone, importUserData))
                .build()
        ).thenApply((FacadeResult<TransactionsResponseBody> result) -> mapper.translate(result, transactionsRestMapper));
    }

    @Override
    @SneakyThrows
    public CompletableFuture getTransactionsWithoutAccountId(
        String serviceSessionPassword,
        String fintechUserId,
        String fintechRedirectURLOK,
        String fintechRedirectURLNOK,
        UUID xRequestID,
        String xTimestampUTC,
        String xRequestSignature,
        String fintechId,
        UUID bankProfileID,
        Boolean xPsuAuthenticationRequired,
        UUID serviceSessionId,
        String createConsentIfNone,
        String importUserData,
        Boolean computePsuIpAddress,
        String psuIpAddress,
        LocalDate dateFrom,
        LocalDate dateTo,
        String entryReferenceFrom,
        String bookingStatus,
        Boolean deltaList,
        Integer page,
        Integer pageSize
    ) {
        return transactions.execute(
            ListTransactionsRequest.builder()
                .facadeServiceable(FacadeServiceableRequest.builder()
                    // Get rid of CGILIB here by copying:
                    .uaContext(userAgentContext.toBuilder().build())
                    .authorization(fintechId)
                    .sessionPassword(serviceSessionPassword)
                    .fintechUserId(fintechUserId)
                    .fintechRedirectUrlOk(fintechRedirectURLOK)
                    .fintechRedirectUrlNok(fintechRedirectURLNOK)
                    .serviceSessionId(serviceSessionId)
                    .requestId(xRequestID)
                    .bankProfileId(bankProfileID)
                    .anonymousPsu(null != xPsuAuthenticationRequired && !xPsuAuthenticationRequired)
                    .build()
                )
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .entryReferenceFrom(entryReferenceFrom)
                .bookingStatus(bookingStatus)
                .deltaList(deltaList)
                .page(page)
                .pageSize(pageSize)
                .extras(getExtras(createConsentIfNone, importUserData))
                .build()
        ).thenApply((FacadeResult<TransactionsResponseBody> result) -> mapper.translate(result, transactionsRestMapper));
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<ResponseEntity<Void>> deleteConsent(UUID serviceSessionId, String serviceSessionPassword, UUID xRequestID,
                                                                 String xTimestampUTC, String xRequestSignature, String fintechId, Boolean deleteAll) {
        return deleteConsent.execute(
                DeleteConsentRequest.builder()
                        .facadeServiceable(FacadeServiceableRequest.builder()
                                // Get rid of CGILIB here by copying:
                                .authorization(fintechId)
                                .sessionPassword(serviceSessionPassword)
                                .serviceSessionId(serviceSessionId)
                                .requestId(xRequestID)
                                .build()
                        )
                        .deleteAll(deleteAll)
                        .build()
        ).thenApply(it -> (ResponseEntity<Void>) mapper.translate(it, body -> null));
    }

    @Override
    public CompletableFuture getAisSessionStatus(UUID serviceSessionId,
                                                 String serviceSessionPassword,
                                                 UUID xRequestID,
                                                 String xTimestampUTC,
                                                 String xRequestSignature,
                                                 String fintechId) {
        return aisSessionStatus.execute(AisAuthorizationStatusRequest.builder()
                .facadeServiceable(FacadeServiceableRequest.builder()
                        .authorization(fintechId)
                        .sessionPassword(serviceSessionPassword)
                        .serviceSessionId(serviceSessionId)
                        .requestId(xRequestID)
                        .build()
                ).build()
        ).thenApply((FacadeResult<AisAuthorizationStatusBody> result) -> mapper.translate(result, sessionStatusToApiMapper));
    }

    @Override
    public CompletableFuture updateExternalAisSession(UUID serviceSessionId,
                                                      String serviceSessionPassword,
                                                      UUID xRequestID,
                                                      String xTimestampUTC,
                                                      String xRequestSignature,
                                                      String fintechId) {
        return updateExternalAis.execute(UpdateExternalAisSessionRequest.builder()
                .facadeServiceable(FacadeServiceableRequest.builder()
                        .authorization(fintechId)
                        .sessionPassword(serviceSessionPassword)
                        .serviceSessionId(serviceSessionId)
                        .requestId(xRequestID)
                        .build()
                ).build()
        ).thenApply((FacadeResult<UpdateExternalAisSessionBody> result) -> mapper.translate(result, updateExternalAisSessionToApiMapper));
    }

    @NotNull
    @SneakyThrows
    private Map<ExtraRequestParam, Object> getExtras(String createConsentIfNone, String importUserData) {
        Map<ExtraRequestParam, Object> extras = new EnumMap<>(ExtraRequestParam.class);
        if (null != createConsentIfNone) {
            extras.put(ExtraRequestParam.CONSENT, objectMapper.readValue(createConsentIfNone, AisConsent.class));
        }
        if (null != importUserData) {
            extras.put(ExtraRequestParam.IMPORT_DATA, importUserData);
        }
        return extras;
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE)
    public interface AccountListFacadeResponseBodyToRestBodyMapper extends FacadeResponseBodyToRestBodyMapper<AccountList, AccountListBody> {
        AccountList map(AccountListBody facadeEntity);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE)
    public interface TransactionsFacadeResponseBodyToRestBodyMapper extends FacadeResponseBodyToRestBodyMapper<TransactionsResponse, TransactionsResponseBody> {

        @Mapping(source = "analytics.bookings", target = "analytics")
        TransactionsResponse map(TransactionsResponseBody facadeEntity);
    }

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE, uses = UuidMapper.class)
    public interface ConsentAuthorizationSessionStatusToApiMapper extends FacadeResponseBodyToRestBodyMapper<SessionStatusDetails, AisAuthorizationStatusBody> {
        SessionStatusDetails map(AisAuthorizationStatusBody facade);
    }

    @Mapper(componentModel = GlobalConst.SPRING_KEYWORD, implementationPackage = Const.API_MAPPERS_PACKAGE, uses = UuidMapper.class)
    public interface UpdateExternalAisSessionToApiMapper extends FacadeResponseBodyToRestBodyMapper<UpdateAisExternalSessionStatus, UpdateExternalAisSessionBody> {
        UpdateAisExternalSessionStatus map(UpdateExternalAisSessionBody facade);
    }
}
