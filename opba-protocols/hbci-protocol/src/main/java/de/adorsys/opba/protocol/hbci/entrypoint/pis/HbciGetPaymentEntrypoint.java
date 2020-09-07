package de.adorsys.opba.protocol.hbci.entrypoint.pis;

import com.google.common.collect.ImmutableMap;
import de.adorsys.multibanking.domain.PaymentStatus;
import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.ok.SuccessResult;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.dto.messages.ProcessResponse;
import de.adorsys.opba.protocol.bpmnshared.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.hbci.context.PaymentHbciContext;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciOutcomeMapper;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_REQUEST_SAGA;

/**
 * Base class for getting payment info and status
 */
@RequiredArgsConstructor
public abstract class HbciGetPaymentEntrypoint<REQUEST extends FacadeServiceableGetter, RESULT_BODY> {
    private final ProtocolAction action;
    private final Function<ProcessResponse, RESULT_BODY> extractResultBodyMapper;
    private final RuntimeService runtimeService;
    private final ProcessEventHandlerRegistrar registrar;
    private final DtoMapper<Set<ValidationIssue>, Set<ValidationError>> errorMapper;
    private final HbciPreparePaymentContext hbciPreparePaymentContext;

    @Transactional
    public CompletableFuture<Result<RESULT_BODY>> execute(ServiceContext<REQUEST> serviceContext) {
        PaymentHbciContext paymentHbciContext = hbciPreparePaymentContext.prepareContext(serviceContext, action);
        if (!paymentHbciContext.getPayment().isInstantPayment()) {
            return acccStatusResult(paymentHbciContext);
        }
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                HBCI_REQUEST_SAGA,
                new ConcurrentHashMap<>(ImmutableMap.of(CONTEXT, paymentHbciContext))
        );

        CompletableFuture<Result<RESULT_BODY>> result = new CompletableFuture<>();

        registrar.addHandler(
                instance.getProcessInstanceId(),
                new HbciOutcomeMapper<>(result, extractResultBodyMapper, errorMapper)
        );

        return result;
    }

    private CompletableFuture<Result<RESULT_BODY>> acccStatusResult(PaymentHbciContext paymentHbciContext) {
        paymentHbciContext.getPayment().setPaymentStatus(PaymentStatus.ACCC.name());
        ProcessResponse processResponse = new ProcessResponse("", "", paymentHbciContext.getPayment());
        Result<RESULT_BODY> result = new SuccessResult<>(extractResultBodyMapper.apply(processResponse));
        return CompletableFuture.completedFuture(result);
    }
}
