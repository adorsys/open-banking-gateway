package de.adorsys.opba.protocol.hbci.entrypoint.pis;

import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentStatusRequest;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.pis.GetPaymentStatusState;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciResultBodyExtractor;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.Set;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.GET_PAYMENT_STATUS;

/**
 * Entry point to get hbci payment status.
 */
@Service("hbciGetPaymentStatusState")
public class HbciGetPaymentStatusEntrypoint extends HbciGetPaymentEntrypoint<PaymentStatusRequest, PaymentStatusBody> implements GetPaymentStatusState {

    public HbciGetPaymentStatusEntrypoint(RuntimeService runtimeService,
                                        ProcessEventHandlerRegistrar registrar,
                                        DtoMapper<Set<ValidationIssue>, Set<ValidationError>> errorMapper,
                                        HbciPrepareContext hbciPrepareContext,
                                        HbciResultBodyExtractor extractor) {
        super(GET_PAYMENT_STATUS, extractor::extractPaymentStatusBody, runtimeService, registrar, errorMapper, hbciPrepareContext);
    }
}
