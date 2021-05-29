package de.adorsys.opba.helpers.protocol.testing.controller;

import de.adorsys.opba.helpers.protocol.testing.service.MapBasedRequestScopedServicesProvider;
import de.adorsys.opba.protocol.api.ais.ListAccounts;
import de.adorsys.opba.protocol.api.dto.context.Context;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
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

    @PostMapping("/{bankId}/{sessionId}/listAccounts/{listAccountsBeanName}")
    public CompletableFuture<Result<AccountListBody>> listAccounts(@PathVariable String bankId,
                                                                   @PathVariable UUID sessionId,
                                                                   @PathVariable String listAccountsBeanName,
                                                                   @RequestBody ListAccountsRequest request) {
        var bean = context.getBean(listAccountsBeanName, ListAccounts.class);
        var ctx = supplyContext(bankId, sessionId, request);
        return bean.execute(supplyServiceContext(sessionId, ctx));
    }

    private <T> Context<T> supplyContext(String bankId, UUID sessionId, T request) {
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
                ""
        );
    }

    @SuppressWarnings("unchecked")
    private <T> ServiceContext<T> supplyServiceContext(UUID sessionId, Context<T> ctx) {
        return ServiceContext.builder()
                .ctx((Context) ctx)
                .requestScoped(servicesProvider.findRegisteredByKeyId(sessionId.toString()))
                .build();
    }
}
