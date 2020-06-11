package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate;

import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeaders;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStartPaymentAuthorizationParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.PaymentInitiationService;
import de.adorsys.xs2a.adapter.service.RequestParams;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.StartScaProcessResponse;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.service.ResponseHeaders.ASPSP_SCA_APPROACH;

/**
 * Initiates the payment authorization. Optionally may provide preferred ASPSP approach.
 */
@Service("xs2aStartPaymentAuthorization")
@RequiredArgsConstructor
public class StartPaymentAuthorization extends ValidatedExecution<Xs2aPisContext> {

    private final Extractor extractor;
    private final Xs2aValidator validator;
    private final PaymentInitiationService pis;
    private final TppRedirectPreferredResolver tppRedirectPreferredResolver;

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aPisContext context) {
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        CurrentBankProfile config = context.aspspProfile();
        ValidatedPathHeaders<Xs2aStartPaymentAuthorizationParameters, Xs2aStandardHeaders> params = extractor.forExecution(context);

        params.getHeaders().setTppRedirectPreferred(tppRedirectPreferredResolver.isRedirectApproachPreferred(config));

        Response<StartScaProcessResponse> scaStart = pis.startSinglePaymentAuthorisation(
                params.getPath().getPaymentProduct(),
                params.getPath().getPaymentId(),
                params.getHeaders().toHeaders(),
                RequestParams.empty(),
                new UpdatePsuAuthentication());

        String aspspSelectedApproach = scaStart.getHeaders().getHeader(ASPSP_SCA_APPROACH);
        context.setAspspScaApproach(null == aspspSelectedApproach ? config.getPreferredApproach().name() : aspspSelectedApproach);
        context.setAuthorizationId(scaStart.getBody().getAuthorisationId());
        context.setStartScaProcessResponse(scaStart.getBody());
        execution.setVariable(CONTEXT, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aPisContext context) {
        CurrentBankProfile config = context.aspspProfile();

        ContextUtil.getAndUpdateContext(execution, (Xs2aPisContext ctx) -> {
            ctx.setAspspScaApproach(config.getPreferredApproach().name());
            ctx.setAuthorizationId(UUID.randomUUID().toString());
        });
    }

    @Service
    public static class Extractor extends PathHeadersMapperTemplate<Xs2aPisContext,
                                                                           Xs2aStartPaymentAuthorizationParameters,
                                                                           Xs2aStandardHeaders> {

        public Extractor(
                DtoMapper<Xs2aContext, Xs2aStandardHeaders> toHeaders,
                DtoMapper<Xs2aPisContext, Xs2aStartPaymentAuthorizationParameters> toParameters) {
            super(toHeaders, toParameters);
        }
    }
}
