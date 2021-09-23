package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate;

import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded.AuthorizationPossibleErrorHandler;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStartPaymentAuthorizationParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded.ProvidePsuPasswordBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.PaymentInitiationService;
import de.adorsys.xs2a.adapter.api.RequestParams;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.PaymentProduct;
import de.adorsys.xs2a.adapter.api.model.PaymentService;
import de.adorsys.xs2a.adapter.api.model.ScaStatus;
import de.adorsys.xs2a.adapter.api.model.StartScaprocessResponse;
import de.adorsys.xs2a.adapter.api.model.UpdatePsuAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.api.ResponseHeaders.ASPSP_SCA_APPROACH;

/**
 * Initiates the payment authorization. Optionally may provide preferred ASPSP approach.
 */
@Slf4j
@Service("xs2aStartPaymentAuthorizationWithPin")
@RequiredArgsConstructor
public class StartPaymentAuthorizationWithPin extends ValidatedExecution<Xs2aPisContext> {

    private final Extractor extractor;
    private final Xs2aValidator validator;
    private final PaymentInitiationService pis;
    private final TppRedirectPreferredResolver tppRedirectPreferredResolver;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());
    private final AuthorizationPossibleErrorHandler errorSink;

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aPisContext context) {
        logResolver.log("doValidate: execution ({}) with context ({})", execution, context);

        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);
        ValidatedPathHeadersBody<Xs2aStartPaymentAuthorizationParameters, Xs2aStandardHeaders, UpdatePsuAuthentication> params = extractor.forExecution(context);
        errorSink.handlePossibleAuthorizationError(
                () -> startAuthorizationWithPin(execution, context, params),
                ex -> aisOnWrongPassword(execution));
    }


    private void startAuthorizationWithPin(DelegateExecution execution, Xs2aPisContext context, ValidatedPathHeadersBody<Xs2aStartPaymentAuthorizationParameters,
            Xs2aStandardHeaders, UpdatePsuAuthentication> params) {
        CurrentBankProfile config = context.aspspProfile();
        params.getHeaders().setTppRedirectPreferred(tppRedirectPreferredResolver.isRedirectApproachPreferred(config));

        logResolver.log("startPaymentAuthorisation with parameters: {}", params.getPath(), params.getHeaders());

        Response<StartScaprocessResponse> scaStart = pis.startPaymentAuthorisation(
                PaymentService.PAYMENTS,
                PaymentProduct.fromValue(params.getPath().getPaymentProduct()),
                params.getPath().getPaymentId(),
                params.getHeaders().toHeaders(),
                RequestParams.empty(),
               params.getBody());

        logResolver.log("startPaymentAuthorisation response: {}", scaStart);

        String aspspSelectedApproach = scaStart.getHeaders().getHeader(ASPSP_SCA_APPROACH);
        context.setAspspScaApproach(null == aspspSelectedApproach ? config.getPreferredApproach().name() : aspspSelectedApproach);
        context.setAuthorizationId(scaStart.getBody().getAuthorisationId());
        context.setStartScaProcessResponse(scaStart.getBody());

        ScaStatus scaStatus = scaStart.getBody().getScaStatus();
        updateContext(execution, scaStart, scaStatus);
        execution.setVariable(CONTEXT, context);
    }

    private void aisOnWrongPassword(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    log.warn("Request {} of {} has provided incorrect password", ctx.getRequestId(), ctx.getSagaId());
                    ctx.setWrongAuthCredentials(true);
                }
        );
    }

    private void updateContext(DelegateExecution execution, Response<StartScaprocessResponse> scaStart, ScaStatus scaStatus) {
        ContextUtil.getAndUpdateContext(
            execution,
            (Xs2aContext ctx) -> {
                ctx.setWrongAuthCredentials(false);
                ctx.setPsuPassword(null); // eagerly destroy password, albeit it is not persisted
                setScaAvailableMethodsIfCanBeChosen(scaStart, ctx);
                ctx.setScaStatus(null == scaStatus ? null : scaStatus.toString());
                ctx.setScaSelected(ScaUtil.scaMethodSelected(scaStart.getBody()));
                ctx.setSelectedScaDecoupled(ScaUtil.isDecoupled(scaStart.getHeaders()));
                ctx.setChallengeData(scaStart.getBody().getChallengeData());
            }
        );
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aPisContext context) {
        CurrentBankProfile config = context.aspspProfile();

        ContextUtil.getAndUpdateContext(execution, (Xs2aPisContext ctx) -> {
            ctx.setAspspScaApproach(null != config.getPreferredApproach() ? config.getPreferredApproach().name() : Approach.REDIRECT.name());
            ctx.setAuthorizationId(UUID.randomUUID().toString());
            ctx.setSelectedScaDecoupled(Approach.DECOUPLED.name().equals(ctx.getAspspScaApproach()));
        });
    }



    @Service
    public static class Extractor extends PathHeadersBodyMapperTemplate<Xs2aPisContext,
            Xs2aStartPaymentAuthorizationParameters,
            Xs2aStandardHeaders,
            ProvidePsuPasswordBody,
            UpdatePsuAuthentication> {

        public Extractor(
                DtoMapper<Xs2aContext, ProvidePsuPasswordBody> toValidatableBody,
                DtoMapper<ProvidePsuPasswordBody, UpdatePsuAuthentication> toBody,
                DtoMapper<Xs2aContext, Xs2aStandardHeaders> toHeaders,
                DtoMapper<Xs2aPisContext, Xs2aStartPaymentAuthorizationParameters> toParameters) {
            super(toValidatableBody, toBody, toHeaders, toParameters);
        }
    }

    private void setScaAvailableMethodsIfCanBeChosen(Response<StartScaprocessResponse> authResponse, Xs2aContext ctx) {
        if (null == authResponse.getBody().getScaMethods()) {
            return;
        }

        ctx.setAvailableSca(
            authResponse.getBody().getScaMethods().stream()
                .map(ScaMethod.FROM_AUTH::map)
                .collect(Collectors.toList())
        );
    }
}
