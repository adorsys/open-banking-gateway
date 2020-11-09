package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.decoupled;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded.ProvidePsuPasswordBody;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.RequestParams;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.ScaStatusResponse;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aAisCheckDecoupledAuthorizationStatus")
@RequiredArgsConstructor
public class Xs2aAisCheckDecoupledAuthorisationStatus extends ValidatedExecution<Xs2aContext> {

    private final AccountInformationService ais;
    private final Extractor extractor;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        ValidatedPathHeadersBody<Xs2aAuthorizedConsentParameters, Xs2aStandardHeaders, UpdatePsuAuthentication> params =
                extractor.forExecution(context);
        Response<ScaStatusResponse> consentScaStatus = ais.getConsentScaStatus(context.getConsentId(),
                                                                               context.getAuthorizationId(),
                                                                               params.getHeaders().toHeaders(),
                                                                               RequestParams.empty());

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    ctx.setDecoupledScaFinished(consentScaStatus.getBody().getScaStatus().isFinalisedStatus());
                    ctx.setScaStatus(consentScaStatus.getBody().getScaStatus().getValue());
                }
        );
    }

    @Service
    public static class Extractor extends PathHeadersBodyMapperTemplate<
                                                                               Xs2aContext,
                                                                               Xs2aAuthorizedConsentParameters,
                                                                               Xs2aStandardHeaders,
                                                                               ProvidePsuPasswordBody,
                                                                               UpdatePsuAuthentication> {

        public Extractor(
                DtoMapper<Xs2aContext, ProvidePsuPasswordBody> toValidatableBody,
                DtoMapper<ProvidePsuPasswordBody, UpdatePsuAuthentication> toBody,
                DtoMapper<Xs2aContext, Xs2aStandardHeaders> toHeaders,
                DtoMapper<Xs2aContext, Xs2aAuthorizedConsentParameters> toParameters) {
            super(toValidatableBody, toBody, toHeaders, toParameters);
        }
    }
}
