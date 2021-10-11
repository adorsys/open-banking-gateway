package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.ScaUtil;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded.ProvidePsuPasswordBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.ScaStatus;
import de.adorsys.xs2a.adapter.api.model.UpdatePsuAuthentication;
import de.adorsys.xs2a.adapter.api.model.UpdatePsuAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Send PSU PIN/Password from the context to ASPSP. The password is typically provided by
 * {@link de.adorsys.opba.protocol.api.authorization.UpdateAuthorization}. Updates available SCA methods after
 * ASPSP returns the result.
 */
@Slf4j
@Service("xs2aAuthenticateUserConsentWithPin")
@RequiredArgsConstructor
public class Xs2aAisAuthenticateUserConsentWithPin extends ValidatedExecution<Xs2aContext> {

    private final Extractor extractor;
    private final Xs2aValidator validator;
    private final AccountInformationService ais;
    private final AuthorizationPossibleErrorHandler errorSink;
    private final Xs2aLogResolver logResolver = new Xs2aLogResolver(getClass());

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doValidate: execution ({}) with context ({})", execution, context);
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        logResolver.log("doRealExecution: execution ({}) with context ({})", execution, context);
        ValidatedPathHeadersBody<Xs2aAuthorizedConsentParameters, Xs2aStandardHeaders, UpdatePsuAuthentication> params =
                extractor.forExecution(context);

        errorSink.handlePossibleAuthorizationError(
                () -> aisAuthorizeWithPassword(execution, params),
                ex -> aisOnWrongPassword(execution)
        );
    }

    private void aisAuthorizeWithPassword(
            DelegateExecution execution,
            ValidatedPathHeadersBody<Xs2aAuthorizedConsentParameters, Xs2aStandardHeaders, UpdatePsuAuthentication> params) {

        logResolver.log("updateConsentsPsuData with parameters: {}", params.getPath(), params.getHeaders(), params.getBody());

        Response<UpdatePsuAuthenticationResponse> authResponse = ais.updateConsentsPsuData(
                params.getPath().getConsentId(),
                params.getPath().getAuthorizationId(),
                params.getHeaders().toHeaders(),
                params.getPath().toParameters(),
                params.getBody()
        );

        logResolver.log("updateConsentsPsuData response: {}", authResponse);

        ScaStatus scaStatus = authResponse.getBody().getScaStatus();

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    ctx.setWrongAuthCredentials(false);
                    ctx.setPsuPassword(null); // eagerly destroy password, albeit it is not persisted
                    setScaAvailableMethodsIfCanBeChosen(authResponse, ctx);
                    ctx.setScaSelected(ScaUtil.scaMethodSelected(authResponse.getBody()));
                    ctx.setSelectedScaDecoupled(ScaUtil.isDecoupled(authResponse.getHeaders()));
                    ctx.setChallengeData(authResponse.getBody().getChallengeData());
                    ctx.setScaStatus(null == scaStatus ? null : scaStatus.toString());
                }
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

    private void setScaAvailableMethodsIfCanBeChosen(
        Response<UpdatePsuAuthenticationResponse> authResponse, Xs2aContext ctx
    ) {
        if (null == authResponse.getBody().getScaMethods()) {
           return;
        }

        ctx.setAvailableSca(
            authResponse.getBody().getScaMethods().stream()
                .map(ScaMethod.FROM_AUTH::map)
                .collect(Collectors.toList())
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
