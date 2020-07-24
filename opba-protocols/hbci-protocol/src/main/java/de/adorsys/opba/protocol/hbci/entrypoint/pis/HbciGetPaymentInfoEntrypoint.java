package de.adorsys.opba.protocol.hbci.entrypoint.pis;

import com.google.common.collect.ImmutableMap;
import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoRequest;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.api.pis.GetPaymentInfoState;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciOutcomeMapper;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciResultBodyExtractor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.GET_PAYMENT_INFORMATION;
import static de.adorsys.opba.protocol.bpmnshared.GlobalConst.CONTEXT;
import static de.adorsys.opba.protocol.hbci.constant.GlobalConst.HBCI_REQUEST_SAGA;

/**
 * Entry point to get hbci payment status.
 */
@Service("hbciGetPaymentInfoState")
@RequiredArgsConstructor
public class HbciGetPaymentInfoEntrypoint implements GetPaymentInfoState {
    private final HbciResultBodyExtractor extractor;
    private final RuntimeService runtimeService;
    private final ProcessEventHandlerRegistrar registrar;
    private final DtoMapper<Set<ValidationIssue>, Set<ValidationError>> errorMapper;
    private final HbciPrepareContext hbciPrepareContext;

    @Transactional
    public CompletableFuture<Result<PaymentInfoBody>> execute(ServiceContext<PaymentInfoRequest> serviceContext) {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                HBCI_REQUEST_SAGA,
                new ConcurrentHashMap<>(ImmutableMap.of(
                        CONTEXT,
                        hbciPrepareContext.prepareContext(serviceContext, GET_PAYMENT_INFORMATION)
                ))
        );

        CompletableFuture<Result<PaymentInfoBody>> result = new CompletableFuture<>();

        registrar.addHandler(
                instance.getProcessInstanceId(),
                new HbciOutcomeMapper<>(result, extractor::extractPaymentInfoBody, errorMapper)
        );

        return result;
    }
}
