package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.decoupled;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedQueryHeaders;
import de.adorsys.opba.protocol.xs2a.service.mapper.QueryHeadersMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import de.adorsys.xs2a.adapter.api.RequestParams;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.ScaStatus;
import de.adorsys.xs2a.adapter.api.model.ScaStatusResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("xs2aAisCheckDecoupledAuthorizationStatus")
@RequiredArgsConstructor
public class Xs2aAisCheckDecoupledAuthorisationStatus extends ValidatedExecution<Xs2aContext> {
    private final RuntimeService runtimeService;

    private final AccountInformationService ais;
    private final Extractor extractor;
    private final ApplicationEventPublisher eventPublisher;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        ValidatedQueryHeaders<Xs2aAuthorizedConsentParameters, Xs2aStandardHeaders> params = extractor.forExecution(context);
        Response<ScaStatusResponse> consentScaStatus = ais.getConsentScaStatus(context.getConsentId(),
                params.getQuery().getAuthorizationId(),
                params.getHeaders().toHeaders(),
                RequestParams.empty());

        ScaStatus scaStatus = consentScaStatus.getBody().getScaStatus();
        DecoupledUtil.postHandleSca(execution, context, scaStatus, eventPublisher);
    }

    @Service
    public static class Extractor extends QueryHeadersMapperTemplate<Xs2aContext,
            Xs2aAuthorizedConsentParameters,
            Xs2aStandardHeaders> {

        public Extractor(
                DtoMapper<Xs2aContext, Xs2aStandardHeaders> toHeaders,
                DtoMapper<Xs2aContext, Xs2aAuthorizedConsentParameters> toParameters) {
            super(toHeaders, toParameters);
        }
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doMockedExecution: execution ({}) with context ({})", execution, context);

        ContextUtil.getAndUpdateContext(execution, (Xs2aContext ctx) -> {
            ctx.setDecoupledScaFinished(true);
            ctx.setScaStatus(ScaStatus.FINALISED.name());
        });
        runtimeService.trigger(execution.getId());
    }
}
