package de.adorsys.opba.protocol.hbci.entrypoint.pis;

import de.adorsys.opba.protocol.api.dto.ValidationIssue;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoBody;
import de.adorsys.opba.protocol.api.dto.request.payments.PaymentInfoRequest;
import de.adorsys.opba.protocol.api.dto.result.body.ValidationError;
import de.adorsys.opba.protocol.api.pis.GetPaymentInfoState;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.eventbus.ProcessEventHandlerRegistrar;
import de.adorsys.opba.protocol.hbci.entrypoint.HbciResultBodyExtractor;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.Set;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.GET_PAYMENT_INFORMATION;

/**
 * Entry point to get hbci payment status.
 */
@Service("hbciGetPaymentInfoState")
public class HbciGetPaymentInfoEntrypoint extends HbciGetPaymentEntrypoint<PaymentInfoRequest, PaymentInfoBody> implements GetPaymentInfoState {

    public HbciGetPaymentInfoEntrypoint(RuntimeService runtimeService,
                                        ProcessEventHandlerRegistrar registrar,
                                        DtoMapper<Set<ValidationIssue>, Set<ValidationError>> errorMapper,
                                        HbciPrepareContext hbciPrepareContext,
                                        HbciResultBodyExtractor extractor) {
        super(GET_PAYMENT_INFORMATION, extractor::extractPaymentInfoBody, runtimeService, registrar, errorMapper, hbciPrepareContext);
    }
}
