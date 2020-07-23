package de.adorsys.opba.protocol.hbci.entrypoint.pis;

import com.google.common.collect.ImmutableMap;
import de.adorsys.multibanking.domain.Bank;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.payments.InitiateSinglePaymentRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusRequest;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.pis.GetPaymentStatusState;
import de.adorsys.opba.protocol.api.services.scoped.consent.ProtocolFacingPayment;
import de.adorsys.opba.protocol.bpmnshared.config.flowable.FlowableObjectMapper;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.hbci.HbciUuidMapper;
import de.adorsys.opba.protocol.hbci.context.PaymentHbciContext;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciExtendWithServiceContext;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciOutcomeMapper;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciResultBodyExtractor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_MAPPERS_PACKAGE;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_REQUEST_SAGA;

/**
 * Entry point to get hbci payment status.
 */
@Service("hbciGetPaymentStatusState")
@RequiredArgsConstructor
public class HbciGetPaymentStatusEntrypoint implements GetPaymentStatusState {
    private final RuntimeService runtimeService;
    private final HbciResultBodyExtractor extractor;
    private final ProcessEventHandlerRegistrar registrar;
    private final HbciGetPaymentStatusEntrypoint.FromRequest request2ContextMapper;
    private final HbciExtendWithServiceContext extender;
    private final DtoMapper<Set<ValidationIssue>, Set<ValidationError>> errorMapper;
    private final FlowableObjectMapper mapper;

    @Override
    @Transactional
    public CompletableFuture<Result<PaymentStatusBody>> execute(ServiceContext<PaymentStatusRequest> serviceContext) {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                HBCI_REQUEST_SAGA,
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, prepareContext(serviceContext)))
        );

        CompletableFuture<Result<PaymentStatusBody>> result = new CompletableFuture<>();

        registrar.addHandler(
                instance.getProcessInstanceId(),
                new HbciOutcomeMapper<>(result, extractor::extractPaymentStatusBody, errorMapper)
        );

        return result;
    }

    @SneakyThrows
    protected PaymentHbciContext prepareContext(ServiceContext<PaymentStatusRequest> serviceContext) {
        PaymentHbciContext context = request2ContextMapper.map(serviceContext.getRequest());
        context.setAction(ProtocolAction.GET_PAYMENT_STATUS);
        extender.extend(context, serviceContext);

        Bank bank = new Bank();
        bank.setBic(serviceContext.getRequestScoped().aspspProfile().getBic());
        bank.setBankCode(serviceContext.getRequestScoped().aspspProfile().getBankCode());
        context.setBank(bank);

        ProtocolFacingPayment payment = serviceContext.getRequestScoped().paymentAccess().getFirstByCurrentSession();
        PaymentHbciContext savedPaymentContext = mapper.getMapper().readValue(payment.getPaymentContext(), PaymentHbciContext.class);
        context.setPayment(savedPaymentContext.getPayment());
        context.setAccountIban(savedPaymentContext.getAccountIban());

        return context;
    }

    /**
     * Mapper to convert incoming user request to processable request context.
     */
    @Mapper(componentModel = SPRING_KEYWORD, uses = HbciUuidMapper.class, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface FromRequest extends DtoMapper<InitiateSinglePaymentRequest, PaymentHbciContext> {

        @Mapping(source = "facadeServiceable.bankId", target = "aspspId")
        @Mapping(source = "facadeServiceable.requestId", target = "requestId")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlOk", target = "fintechRedirectUriOk")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlNok", target = "fintechRedirectUriNok")
        PaymentHbciContext map(PaymentStatusRequest ctx);
    }
}
