package de.adorsys.opba.protocol.hbci.entrypoint.ais;

import com.google.common.collect.ImmutableMap;
import de.adorsys.multibanking.domain.Bank;
import de.adorsys.opba.protocol.api.ais.ListAccounts;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.accounts.ListAccountsRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AccountListBody;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.hbci.HbciUuidMapper;
import de.adorsys.opba.protocol.hbci.context.AccountListHbciContext;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciExtendWithServiceContext;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciOutcomeMapper;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciResultBodyExtractor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_MAPPERS_PACKAGE;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_REQUEST_SAGA;

/**
 * Entry point that handles ListAccounts request from the FinTech. Prepares the context and triggers BPMN engine for
 * further actions.
 */
@Service("hbciListAccounts")
@RequiredArgsConstructor
public class HbciListAccountsEntrypoint implements ListAccounts {

    private final RuntimeService runtimeService;
    private final HbciResultBodyExtractor extractor;
    private final ProcessEventHandlerRegistrar registrar;
    private final FromRequest mapper;
    private final HbciExtendWithServiceContext extender;
    private final DtoMapper<Set<ValidationIssue>, Set<ValidationError>> errorMapper;

    @Override
    public CompletableFuture<Result<AccountListBody>> execute(ServiceContext<ListAccountsRequest> serviceContext) {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                HBCI_REQUEST_SAGA,
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, prepareContext(serviceContext)))
        );

        CompletableFuture<Result<AccountListBody>> result = new CompletableFuture<>();

        registrar.addHandler(
                instance.getProcessInstanceId(),
                new HbciOutcomeMapper<>(result, extractor::extractAccountList, errorMapper)
        );
        return result;
    }

    protected AccountListHbciContext prepareContext(ServiceContext<ListAccountsRequest> serviceContext) {
        AccountListHbciContext context = mapper.map(serviceContext.getRequest());
        context.setAction(ProtocolAction.LIST_ACCOUNTS);
        extender.extend(context, serviceContext);
        Bank bank = new Bank();
        bank.setBic(serviceContext.getRequestScoped().aspspProfile().getBic());
        bank.setBankCode(serviceContext.getRequestScoped().aspspProfile().getBankCode());
        context.setBank(bank);
        return context;
    }

    /**
     * Mapper to convert incoming user request to processable request context.
     */
    @Mapper(componentModel = SPRING_KEYWORD, uses = HbciUuidMapper.class, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface FromRequest extends DtoMapper<ListAccountsRequest, AccountListHbciContext> {

        @Mapping(source = "facadeServiceable.bankProfileId", target = "aspspId")
        @Mapping(source = "facadeServiceable.requestId", target = "requestId")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlOk", target = "fintechRedirectUriOk")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlNok", target = "fintechRedirectUriNok")
        @Mapping(source = "facadeServiceable.online", target = "online")
        AccountListHbciContext map(ListAccountsRequest ctx);
    }
}
