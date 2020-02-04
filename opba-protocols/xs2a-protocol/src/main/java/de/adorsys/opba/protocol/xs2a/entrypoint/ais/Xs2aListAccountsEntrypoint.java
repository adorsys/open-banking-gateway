package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.protocol.api.ListAccounts;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.xs2a.entrypoint.OutcomeMapper;
import de.adorsys.opba.protocol.xs2a.entrypoint.Xs2aResultBodyExtractor;
import de.adorsys.opba.protocol.xs2a.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.REQUEST_SAGA;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Service("xs2aListAccounts")
@RequiredArgsConstructor
public class Xs2aListAccountsEntrypoint implements ListAccounts {

    private final RuntimeService runtimeService;
    private final Xs2aResultBodyExtractor extractor;
    private final ProcessEventHandlerRegistrar registrar;
    private final Xs2aListAccountsEntrypoint.FromRequest mapper;

    @Override
    public CompletableFuture<Result<AccountListBody>> execute(ServiceContext<ListAccountsRequest> serviceContext) {
        Xs2aContext context = mapper.map(serviceContext.getRequest());
        context.setAction(ProtocolAction.LIST_ACCOUNTS);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                REQUEST_SAGA,
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, context))
        );

        CompletableFuture<Result<AccountListBody>> result = new CompletableFuture<>();

        registrar.addHandler(
                instance.getProcessInstanceId(),
                new OutcomeMapper<>(result, extractor::extractAccountList)
        );
        return result;
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromRequest extends DtoMapper<ListAccountsRequest, Xs2aContext> {

        @Mapping(source = "facadeServiceable.bankID", target = "aspspId")
        @Mapping(source = "facadeServiceable.uaContext.psuIpAddress", target = "psuIpAddress")
        @Mapping(source = "facadeServiceable.uaContext.psuAccept", target = "contentType", nullValuePropertyMappingStrategy = IGNORE)
        Xs2aContext map(ListAccountsRequest ctx);
    }
}
