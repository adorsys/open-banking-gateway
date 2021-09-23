package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate;

import de.adorsys.opba.protocol.api.common.Approach;
import de.adorsys.opba.protocol.api.common.CurrentBankProfile;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded.AuthorizationPossibleErrorHandler;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aInitialConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded.ProvidePsuPasswordBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import de.adorsys.xs2a.adapter.api.Response;
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
 * Initiates the consent authorization with pin. Optionally may provide preferred ASPSP approach.
 */
@Slf4j
@Service("xs2aStartConsentAuthorizationWithPin")
@RequiredArgsConstructor
public class StartConsentAuthorizationWithPin extends ValidatedExecution<Xs2aContext> {

    private final Extractor extractor;
    private final Xs2aValidator validator;
    private final AccountInformationService ais;
    private final TppRedirectPreferredResolver tppRedirectPreferredResolver;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());
    private final AuthorizationPossibleErrorHandler errorSink;

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doValidate: execution ({}) with context ({})", execution, context);
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }


    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doMockedExecution: execution ({}) with context ({})", execution, context);
        CurrentBankProfile config = context.aspspProfile();
        ContextUtil.getAndUpdateContext(execution, (Xs2aContext ctx) -> {
            ctx.setAspspScaApproach(null != config.getPreferredApproach() ? config.getPreferredApproach().name() : Approach.REDIRECT.name());
            ctx.setSelectedScaDecoupled(Approach.DECOUPLED.name().equals(ctx.getAspspScaApproach()));
            ctx.setAuthorizationId(UUID.randomUUID().toString());
        });
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);

        ValidatedPathHeadersBody<Xs2aInitialConsentParameters, Xs2aStandardHeaders, UpdatePsuAuthentication> params = extractor.forExecution(context);
        errorSink.handlePossibleAuthorizationError(
                () -> startAuthorizationWithPin(execution, context, params),
                ex -> aisOnWrongPassword(execution)
        );
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

    private void startAuthorizationWithPin(DelegateExecution execution, Xs2aContext context,
                                           ValidatedPathHeadersBody<Xs2aInitialConsentParameters, Xs2aStandardHeaders, UpdatePsuAuthentication> params) {

        CurrentBankProfile config = context.aspspProfile();
        params.getHeaders().setTppRedirectPreferred(tppRedirectPreferredResolver.isRedirectApproachPreferred(config));

        logResolver.log("startConsentAuthorisation with parameters: {}", params.getPath(), params.getHeaders());

        Response<StartScaprocessResponse> scaStart = ais.startConsentAuthorisation(
                params.getPath().getConsentId(),
                params.getHeaders().toHeaders(),
                params.getPath().toParameters(),
                params.getBody()
        );

        logResolver.log("startConsentAuthorisation response: {}", scaStart);

        context.setAuthorizationId(scaStart.getBody().getAuthorisationId());
        context.setStartScaProcessResponse(scaStart.getBody());
        String aspspSelectedApproach = scaStart.getHeaders().getHeader(ASPSP_SCA_APPROACH);
        context.setAspspScaApproach(null == aspspSelectedApproach ? config.getPreferredApproach().name() : aspspSelectedApproach);
        ScaStatus scaStatus = scaStart.getBody().getScaStatus();
        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    ctx.setWrongAuthCredentials(false);
                    ctx.setPsuPassword(null); // eagerly destroy password, albeit it is not persisted
                    setScaAvailableMethodsIfCanBeChosen(scaStart, ctx);
                    ctx.setScaSelected(ScaUtil.scaMethodSelected(scaStart.getBody()));
                    ctx.setAuthorizationId(scaStart.getBody().getAuthorisationId());
                    ctx.setSelectedScaDecoupled(ScaUtil.isDecoupled(scaStart.getHeaders()));
                    ctx.setChallengeData(scaStart.getBody().getChallengeData());
                    ctx.setStartScaProcessResponse(scaStart.getBody());
                    ctx.setScaStatus(null == scaStatus ? null : scaStatus.toString());
                }
        );
        execution.setVariable(CONTEXT, context);
    }


    @Service
    public static class Extractor extends PathHeadersBodyMapperTemplate<
            Xs2aContext,
            Xs2aInitialConsentParameters,
            Xs2aStandardHeaders,
            ProvidePsuPasswordBody,
            UpdatePsuAuthentication> {

        public Extractor(
                DtoMapper<Xs2aContext, ProvidePsuPasswordBody> toValidatableBody,
                DtoMapper<ProvidePsuPasswordBody, UpdatePsuAuthentication> toBody,
                DtoMapper<Xs2aContext, Xs2aStandardHeaders> toHeaders,
                DtoMapper<Xs2aContext, Xs2aInitialConsentParameters> toParameters) {
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
