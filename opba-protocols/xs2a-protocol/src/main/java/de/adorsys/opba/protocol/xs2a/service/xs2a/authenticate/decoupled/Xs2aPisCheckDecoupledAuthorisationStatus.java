package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.decoupled;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.mapper.QueryHeadersMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedPaymentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.xs2a.adapter.api.PaymentInitiationService;
import de.adorsys.xs2a.adapter.api.RequestParams;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.PaymentProduct;
import de.adorsys.xs2a.adapter.api.model.PaymentService;
import de.adorsys.xs2a.adapter.api.model.ScaStatus;
import de.adorsys.xs2a.adapter.api.model.ScaStatusResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("xs2aPisCheckDecoupledAuthorizationStatus")
@RequiredArgsConstructor
public class Xs2aPisCheckDecoupledAuthorisationStatus extends ValidatedExecution<Xs2aPisContext> {

    private final PaymentInitiationService pis;
    private final Extractor extractor;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        ValidatedQueryHeaders<Xs2aAuthorizedPaymentParameters, Xs2aStandardHeaders> params = extractor.forExecution(context);
        Response<ScaStatusResponse> paymentInitiationScaStatus = pis.getPaymentInitiationScaStatus(
                PaymentService.fromValue(params.getQuery().getPaymentType().getValue()),
                PaymentProduct.fromValue(params.getQuery().getPaymentProduct()),
                params.getQuery().getPaymentId(),
                params.getQuery().getAuthorizationId(),
                params.getHeaders().toHeaders(),
                RequestParams.empty()
        );

        ScaStatus scaStatus = paymentInitiationScaStatus.getBody().getScaStatus();
        DecoupledUtil.postHandleSca(execution, context, scaStatus, eventPublisher);
    }

    @Service
    public static class Extractor extends QueryHeadersMapperTemplate<Xs2aPisContext,
                Xs2aAuthorizedPaymentParameters,
                Xs2aStandardHeaders> {

        public Extractor(
                DtoMapper<Xs2aContext, Xs2aStandardHeaders> toHeaders,
                DtoMapper<Xs2aPisContext, Xs2aAuthorizedPaymentParameters> toParameters) {
            super(toHeaders, toParameters);
        }
    }
}
