package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.ais.ListAccounts;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.bpmnshared.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.xs2a.context.ais.AccountListXs2aContext;
import de.adorsys.opba.protocol.xs2a.entrypoint.ExtendWithServiceContext;
import de.adorsys.opba.protocol.xs2a.entrypoint.Xs2aOutcomeMapper;
import de.adorsys.opba.protocol.xs2a.entrypoint.Xs2aResultBodyExtractor;
import de.adorsys.opba.protocol.xs2a.entrypoint.helpers.UuidMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.REQUEST_SAGA;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * Entry point that handles ListAccounts request from the FinTech. Prepares the context and triggers BPMN engine for
 * further actions.
 */
@Service("xs2aListAccounts")
@RequiredArgsConstructor
public class Xs2aListAccountsEntrypoint implements ListAccounts {

    private final RuntimeService runtimeService;
    private final Xs2aResultBodyExtractor extractor;
    private final ProcessEventHandlerRegistrar registrar;
    private final Xs2aListAccountsEntrypoint.FromRequest mapper;
    private final ExtendWithServiceContext extender;
    private final DtoMapper<Set<ValidationIssue>, Set<ValidationError>> errorMapper;

    @Override
    public CompletableFuture<Result<AccountListBody>> execute(ServiceContext<ListAccountsRequest> serviceContext) {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                REQUEST_SAGA,
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, prepareContext(serviceContext)))
        );

        CompletableFuture<Result<AccountListBody>> result = new CompletableFuture<>();

        registrar.addHandler(
                instance.getProcessInstanceId(),
                new Xs2aOutcomeMapper<>(result, extractor::extractAccountList, errorMapper)
        );
        return result;
    }

    protected AccountListXs2aContext prepareContext(ServiceContext<ListAccountsRequest> serviceContext) {
        AccountListXs2aContext context = mapper.map(serviceContext.getRequest());
        context.setAction(ProtocolAction.LIST_ACCOUNTS);
        extender.extend(context, serviceContext);
        return context;
    }

    /**
     * Mapper to convert incoming user request to processable request context.
     */
    @Mapper(componentModel = SPRING_KEYWORD, uses = UuidMapper.class, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromRequest extends DtoMapper<ListAccountsRequest, AccountListXs2aContext> {

        @Mapping(source = "facadeServiceable.bankId", target = "aspspId")
        @Mapping(source = "facadeServiceable.requestId", target = "requestId")
        @Mapping(source = "facadeServiceable.uaContext.psuIpAddress", target = "psuIpAddress")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlOk", target = "fintechRedirectUriOk")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlNok", target = "fintechRedirectUriNok")
        @Mapping(source = "facadeServiceable.uaContext.psuAccept", target = "contentType", nullValuePropertyMappingStrategy = IGNORE)
        AccountListXs2aContext map(ListAccountsRequest ctx);
    }
}
