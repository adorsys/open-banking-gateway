package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.ais.ListTransactions;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.transactions.ListTransactionsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.TransactionsResponseBody;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.bpmnshared.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.xs2a.context.ais.TransactionListXs2aContext;
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
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_REQUEST_SAGA;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * Entry point that handles ListTransactions request from the FinTech. Prepares the context and triggers BPMN engine for
 * further actions.
 */
@Service("xs2aListTransactions")
@RequiredArgsConstructor
public class Xs2aListTransactionsEntrypoint implements ListTransactions {

    private final RuntimeService runtimeService;
    private final Xs2aResultBodyExtractor extractor;
    private final ProcessEventHandlerRegistrar registrar;
    private final Xs2aListTransactionsEntrypoint.FromRequest mapper;
    private final ExtendWithServiceContext extender;
    private final DtoMapper<Set<ValidationIssue>, Set<ValidationError>> errorMapper;

    @Override
    public CompletableFuture<Result<TransactionsResponseBody>> execute(ServiceContext<ListTransactionsRequest> serviceContext) {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                XS2A_REQUEST_SAGA,
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, prepareContext(serviceContext)))
        );

        CompletableFuture<Result<TransactionsResponseBody>> result = new CompletableFuture<>();

        registrar.addHandler(
                instance.getProcessInstanceId(),
                new Xs2aOutcomeMapper<>(result, extractor::extractTransactionsReport, errorMapper)
        );

        return result;
    }

    protected TransactionListXs2aContext prepareContext(ServiceContext<ListTransactionsRequest> serviceContext) {
        TransactionListXs2aContext context = mapper.map(serviceContext.getRequest());
        context.setAction(ProtocolAction.LIST_TRANSACTIONS);
        extender.extend(context, serviceContext);
        return context;
    }

    /**
     * Mapper to convert incoming user request to processable request context.
     */
    @Mapper(componentModel = SPRING_KEYWORD, uses = UuidMapper.class, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromRequest extends DtoMapper<ListTransactionsRequest, TransactionListXs2aContext> {

        @Mapping(source = "accountId", target = "resourceId")
        @Mapping(source = "facadeServiceable.requestId", target = "requestId")
        @Mapping(source = "facadeServiceable.bankId", target = "aspspId")
        @Mapping(source = "facadeServiceable.uaContext.psuIpAddress", target = "psuIpAddress")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlOk", target = "fintechRedirectUriOk")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlNok", target = "fintechRedirectUriNok")
        @Mapping(source = "facadeServiceable.uaContext.psuAccept", target = "contentType", nullValuePropertyMappingStrategy = IGNORE)
        TransactionListXs2aContext map(ListTransactionsRequest ctx);
    }
}
