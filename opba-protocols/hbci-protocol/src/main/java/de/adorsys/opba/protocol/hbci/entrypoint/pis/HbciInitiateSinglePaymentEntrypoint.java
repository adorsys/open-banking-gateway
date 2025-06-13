package de.adorsys.opba.protocol.hbci.entrypoint.pis;

import com.google.common.collect.ImmutableMap;
import de.adorsys.multibanking.domain.Bank;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.payments.InitiateSinglePaymentRequest;
import de.adorsys.opba.protocol.api.dto.request.payments.SinglePaymentBody;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.pis.SinglePayment;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.hbci.HbciUuidMapper;
import de.adorsys.opba.protocol.hbci.context.PaymentHbciContext;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciExtendWithServiceContext;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciOutcomeMapper;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciResultBodyExtractor;
import de.adorsys.opba.protocol.hbci.service.protocol.pis.dto.PaymentInitiateBody;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_MAPPERS_PACKAGE;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_REQUEST_SAGA;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Service("hbciInitiateSinglePayment")
@RequiredArgsConstructor
public class HbciInitiateSinglePaymentEntrypoint implements SinglePayment {

    private final RuntimeService runtimeService;
    private final HbciResultBodyExtractor extractor;
    private final ProcessEventHandlerRegistrar registrar;
    private final HbciInitiateSinglePaymentEntrypoint.FromRequest mapper;
    private final HbciExtendWithServiceContext extender;
    private final DtoMapper<Set<ValidationIssue>, Set<ValidationError>> errorMapper;

    @Override
    public CompletableFuture<Result<SinglePaymentBody>> execute(ServiceContext<InitiateSinglePaymentRequest> serviceContext) {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                HBCI_REQUEST_SAGA,
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, prepareContext(serviceContext)))
        );

        if (serviceContext.getRequestScoped().aspspProfile().isUniquePaymentPurpose()) {
            SinglePaymentBody singlePaymentBody = serviceContext.getRequest().getSinglePayment();
            singlePaymentBody.setRemittanceInformationUnstructured(
                    singlePaymentBody.getRemittanceInformationUnstructured() + " " + LocalDateTime.now()
            );
        }

        CompletableFuture<Result<SinglePaymentBody>> result = new CompletableFuture<>();

        registrar.addHandler(
                instance.getProcessInstanceId(),
                new HbciOutcomeMapper<>(result, extractor::extractSinglePaymentBody, errorMapper)
        );

        return result;
    }

    protected PaymentHbciContext prepareContext(ServiceContext<InitiateSinglePaymentRequest> serviceContext) {
        PaymentHbciContext context = mapper.map(serviceContext.getRequest());
        context.setAction(ProtocolAction.SINGLE_PAYMENT);
        extender.extend(context, serviceContext);

        Bank bank = new Bank();
        bank.setBic(serviceContext.getRequestScoped().aspspProfile().getBic());
        bank.setBankCode(serviceContext.getRequestScoped().aspspProfile().getBankCode());
        context.setBank(bank);

        context.setAccountIban(serviceContext.getRequest().getSinglePayment().getDebtorAccount().getIban());

        return context;
    }

    /**
     * Mapper to convert incoming user request to processable request context.
     */
    @Mapper(componentModel = SPRING_KEYWORD, uses = HbciUuidMapper.class, implementationPackage = HBCI_MAPPERS_PACKAGE)
    public interface FromRequest extends DtoMapper<InitiateSinglePaymentRequest, PaymentHbciContext> {

        @Mapping(source = "facadeServiceable.bankProfileId", target = "aspspId")
        @Mapping(source = "facadeServiceable.requestId", target = "requestId")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlOk", target = "fintechRedirectUriOk")
        @Mapping(source = "facadeServiceable.fintechRedirectUrlNok", target = "fintechRedirectUriNok")
        @Mapping(source = "singlePayment", target = "payment", nullValuePropertyMappingStrategy = IGNORE)
        PaymentHbciContext map(InitiateSinglePaymentRequest ctx);

        @Mapping(expression = "java(payment.getPaymentProduct().toString().contains(\"instant\"))", target = "instantPayment")
        PaymentInitiateBody map(SinglePaymentBody payment);
    }
}
