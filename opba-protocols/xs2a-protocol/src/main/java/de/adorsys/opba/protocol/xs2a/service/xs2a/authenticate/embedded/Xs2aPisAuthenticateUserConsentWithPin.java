package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedPaymentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded.ProvidePsuPasswordBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.PaymentInitiationService;
import de.adorsys.xs2a.adapter.service.RequestParams;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.ScaStatus;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthenticationResponse;
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
@Service("xs2aPisAuthenticateUserWithPin")
@RequiredArgsConstructor
public class Xs2aPisAuthenticateUserConsentWithPin extends ValidatedExecution<Xs2aPisContext> {

    private final Extractor extractor;
    private final Xs2aValidator validator;
    private final PaymentInitiationService pis;
    private final AuthorizationPossibleErrorHandler errorSink;

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aPisContext context) {
        validator.validate(execution, context, this.getClass(), extractor.forValidation(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        ValidatedPathHeadersBody<Xs2aAuthorizedPaymentParameters, Xs2aStandardHeaders, UpdatePsuAuthentication> params = extractor.forExecution(context);

        errorSink.handlePossibleAuthorizationError(
                () -> pisAuthorizeWithPassword(execution, params),
                ex -> pisOnWrongPassword(execution)
        );
    }

    private void pisAuthorizeWithPassword(
            DelegateExecution execution,
            ValidatedPathHeadersBody<Xs2aAuthorizedPaymentParameters, Xs2aStandardHeaders, UpdatePsuAuthentication>  params) {

        Response<UpdatePsuAuthenticationResponse> authResponse = pis.updatePaymentPsuData(
                params.getPath().getPaymentType().getValue(),
                params.getPath().getPaymentProduct(),
                params.getPath().getPaymentId(),
                params.getPath().getAuthorizationId(),
                params.getHeaders().toHeaders(),
                RequestParams.empty(),
                params.getBody()
        );

        ScaStatus scaStatus = authResponse.getBody().getScaStatus();

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aPisContext ctx) -> {
                    ctx.setWrongAuthCredentials(false);
                    ctx.setPsuPassword(null); // eagerly destroy password, albeit it is not persisted
                    setScaAvailableMethodsIfCanBeChosen(authResponse, ctx);
                    ctx.setScaSelected(authResponse.getBody().getChosenScaMethod());
                    ctx.setChallengeData(authResponse.getBody().getChallengeData());
                    ctx.setScaStatus(null == scaStatus ? null : scaStatus.getValue());
                }
        );
    }

    private void pisOnWrongPassword(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    log.warn("Request {} of {} has provided incorrect password", ctx.getRequestId(), ctx.getSagaId());
                    ctx.setWrongAuthCredentials(true);
                }
        );
    }

    private void setScaAvailableMethodsIfCanBeChosen(
            Response<UpdatePsuAuthenticationResponse> authResponse, Xs2aPisContext ctx
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
    public static class Extractor extends PathHeadersBodyMapperTemplate<Xs2aPisContext,
                                                                               Xs2aAuthorizedPaymentParameters,
                                                                               Xs2aStandardHeaders,
                                                                               ProvidePsuPasswordBody,
                                                                               UpdatePsuAuthentication> {

        public Extractor(
                DtoMapper<Xs2aContext, ProvidePsuPasswordBody> toValidatableBody,
                DtoMapper<ProvidePsuPasswordBody, UpdatePsuAuthentication> toBody,
                DtoMapper<Xs2aContext, Xs2aStandardHeaders> toHeaders,
                DtoMapper<Xs2aPisContext, Xs2aAuthorizedPaymentParameters> toParameters) {
            super(toValidatableBody, toBody, toHeaders, toParameters);
        }
    }
}
