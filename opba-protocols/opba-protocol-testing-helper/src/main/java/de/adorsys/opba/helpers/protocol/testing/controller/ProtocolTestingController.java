package de.adorsys.opba.helpers.protocol.testing.controller;

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
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class ProtocolTestingController {

    private final ApplicationContext context;
    private final MapBasedRequestScopedServicesProvider servicesProvider;

    @PostMapping("/{sessionId}/listAccounts/{listAccountsBeanName}")
    public CompletableFuture<Result<AccountListBody>> listAccounts(@PathVariable UUID sessionId,
                                                                   @PathVariable String listAccountsBeanName,
                                                                   @RequestBody Request<ListAccountsRequest> request) {
        return executeRequest(sessionId, listAccountsBeanName, request, ListAccounts.class);
    }

    @PostMapping("/{sessionId}/updateAuthorization/{updateAuthorizationBeanName}")
    public CompletableFuture<Result<UpdateAuthBody>> updateAuthorization(
            @PathVariable UUID sessionId, @PathVariable String updateAuthorizationBeanName,
            @RequestBody Request<AuthorizationRequest> request
    ) {
        return executeRequest(sessionId, updateAuthorizationBeanName, request, UpdateAuthorization.class);
    }

    private <T, A extends Action<T, R>, R> CompletableFuture<Result<R>> executeRequest(
            UUID sessionId, String beanName, Request<T> request, Class<A> actionClass) {
        var bean = context.getBean(beanName, actionClass);
        var ctx = supplyContext(
                request.getBank().getId().toString(),
                sessionId,
                request.getRequest(),
                request.getAuthContext()
        );
        var services = servicesProvider.getRequestScopedFor(sessionId.toString());
        services.setBankProfile(request.getBank());
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
}
