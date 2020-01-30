package de.adorsys.opba.protocol.xs2a.entrypoint.ais;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.protocol.api.ListAccounts;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.ErrorResult;
import de.adorsys.opba.protocol.api.dto.result.RedirectionResult;
import de.adorsys.opba.protocol.api.dto.result.Result;
import de.adorsys.opba.protocol.api.dto.result.SuccessResult;
import de.adorsys.opba.protocol.api.dto.result.ValidationErrorResult;
import de.adorsys.opba.protocol.xs2a.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.tppbankingapi.ais.model.generated.AccountList;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.REQUEST_SAGA;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Service("xs2aListAccounts")
@RequiredArgsConstructor
public class Xs2aListAccountsEntrypoint implements ListAccounts {

    private final RuntimeService runtimeService;
    private final Xs2aResultExtractor extractor;
    private final ProcessEventHandlerRegistrar registrar;
    private final Xs2aListAccountsEntrypoint.FromRequest mapper;

    @Override
    public CompletableFuture<Result<AccountList>> list(ListAccountsRequest request) {
        Xs2aContext context = mapper.map(request);
        context.setAction(ProtocolAction.LIST_ACCOUNTS);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                REQUEST_SAGA,
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, context))
        );

        // TODO: Duplicated code
        CompletableFuture<Result<AccountList>> result = new CompletableFuture<>();

        registrar.addHandler(
                instance.getProcessInstanceId(),
                success -> result.complete(new SuccessResult<>(extractor.extractAccountList(success))),
                redir -> result.complete(new RedirectionResult<>(URI.create(redir.getRedirectUri()))),
                validation -> result.complete(new ValidationErrorResult<>(validation.getProvideMoreParamsDialog())),
                error -> result.complete(new ErrorResult<>())
        );

        return result;
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromRequest extends DtoMapper<ListAccountsRequest, Xs2aContext> {

        @Mapping(source = "bankID", target = "aspspId")
        @Mapping(source = "uaContext.psuIpAddress", target = "psuIpAddress")
        @Mapping(source = "uaContext.psuAccept", target = "contentType")
        Xs2aContext map(ListAccountsRequest ctx);
    }
}
