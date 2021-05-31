package de.adorsys.opba.helpers.protocol.testing.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.helpers.protocol.testing.service.MapBasedAspspRepository;
import de.adorsys.opba.helpers.protocol.testing.service.MapBasedRequestScopedServicesProvider;
import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.ais.ListAccounts;
import de.adorsys.opba.protocol.api.authorization.UpdateAuthorization;
import de.adorsys.opba.protocol.api.dto.context.Context;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingConsent;
import de.adorsys.xs2a.adapter.api.model.Aspsp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class ProtocolTestingController {

    private static final ObjectMapper CUSTOM_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final ApplicationContext context;
    private final MapBasedAspspRepository aspspRepository;
    private final MapBasedRequestScopedServicesProvider servicesProvider;

    @PostMapping("/{sessionId}/listAccounts/{listAccountsBeanName}")
    public CompletableFuture<Result<AccountListBody>> listAccounts(@PathVariable UUID sessionId,
                                                                   @PathVariable String listAccountsBeanName,
                                                                   @RequestBody Request<ListAccountsRequest> request) {
        return executeRequest(sessionId, listAccountsBeanName, request, ListAccounts.class);
    }

    @PostMapping("/{sessionId}/updateAuthorization/{updateAuthorizationBeanName}")
    public CompletableFuture<WrappedResult<Result<UpdateAuthBody>>> updateAuthorization(
            @PathVariable UUID sessionId, @PathVariable String updateAuthorizationBeanName,
            @RequestBody Request<AuthorizationRequest> request
    ) {
        return executeRequest(sessionId, updateAuthorizationBeanName, request, UpdateAuthorization.class)
                .thenApply(it -> new WrappedResult<>(
                        it,
                        servicesProvider.getRequestScopedFor(sessionId.toString()).consentAccess().findSingleByCurrentServiceSession().orElse(null))
                );
    }

    private <T, A extends Action<T, R>, R> CompletableFuture<Result<R>> executeRequest(
            UUID sessionId, String beanName, Request<T> request, Class<A> actionClass) {
        var bean = context.getBean(beanName, actionClass);
        var ctx = supplyContext(
                request.getBank().getUuid(),
                sessionId,
                request.getRequest(),
                request.getAuthContext()
        );
        var services = servicesProvider.getRequestScopedFor(sessionId.toString());
        services.setBankProfile(request.getBank());
        if (null != request.getBank()) {
            var aspsp = CUSTOM_MAPPER.convertValue(request.getBank(), Aspsp.class);
            aspspRepository.setAspsp(request.getBank().getUuid(), aspsp);
        }
        services.getConsentAccessor().setConsent(request.getConsent());
        return bean.execute(supplyServiceContext(sessionId, ctx));
    }

    private <T> Context<T> supplyContext(String bankId, UUID sessionId, T request, String authCtx) {
        return new Context<>(
                0L,
                0L,
                bankId,
                sessionId,
                sessionId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                request,
                authCtx
        );
    }

    @SuppressWarnings("unchecked")
    private <T> ServiceContext<T> supplyServiceContext(UUID sessionId, Context<T> ctx) {
        return ServiceContext.builder()
                .ctx((Context) ctx)
                .requestScoped(servicesProvider.findRegisteredByKeyId(sessionId.toString()))
                .build();
    }

    @Data
    public static class Request<T> {
        private T request;
        private MapBasedRequestScopedServicesProvider.Consent consent;
        private MapBasedRequestScopedServicesProvider.BankProfile bank;
        private String authContext;
    }

    @Data
    @AllArgsConstructor
    public static class WrappedResult<T> {
        private T result;
        private ProtocolFacingConsent consent;
    }
}
