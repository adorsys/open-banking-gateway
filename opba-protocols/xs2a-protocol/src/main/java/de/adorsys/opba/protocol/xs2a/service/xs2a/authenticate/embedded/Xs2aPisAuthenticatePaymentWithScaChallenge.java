package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedPaymentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded.ProvideScaChallengeResultBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.api.PaymentInitiationService;
import de.adorsys.xs2a.adapter.api.RequestParams;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.model.PaymentProduct;
import de.adorsys.xs2a.adapter.api.model.PaymentService;
import de.adorsys.xs2a.adapter.api.model.ScaStatusResponse;
import de.adorsys.xs2a.adapter.api.model.TransactionAuthorisation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Send SCA/TAN challenge result from the context to ASPSP. The password is typically provided by
 * {@link de.adorsys.opba.protocol.api.authorization.UpdateAuthorization}.
 */
@Slf4j
@Service("xs2aAuthenticatePaymentWithScaChallenge")
@RequiredArgsConstructor
public class Xs2aPisAuthenticatePaymentWithScaChallenge extends ValidatedExecution<Xs2aPisContext> {

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
        ValidatedPathHeadersBody<Xs2aAuthorizedPaymentParameters, Xs2aStandardHeaders, TransactionAuthorisation> params = extractor.forExecution(context);

        errorSink.handlePossibleAuthorizationError(
                () -> pisAuthorizeWithSca(execution, params),
                ex -> pisOnWrongSca(execution)
        );
    }

    private void pisAuthorizeWithSca(
            DelegateExecution execution,
            ValidatedPathHeadersBody<Xs2aAuthorizedPaymentParameters, Xs2aStandardHeaders, TransactionAuthorisation> params) {

        Response<ScaStatusResponse> authResponse = pis.updatePaymentPsuData(
                PaymentService.fromValue(params.getPath().getPaymentType().getValue()),
                PaymentProduct.fromValue(params.getPath().getPaymentProduct()),
                params.getPath().getPaymentId(),
                params.getPath().getAuthorizationId(),
                params.getHeaders().toHeaders(),
                RequestParams.empty(),
                params.getBody()
        );

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    ctx.setLastScaChallenge(null); // eagerly destroy SCA challenge, albeit it is not persisted
                    ctx.setWrongAuthCredentials(false);
                    ctx.setScaStatus(authResponse.getBody().getScaStatus().toString());
                }
        );
    }

    private void pisOnWrongSca(DelegateExecution execution) {
        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    log.warn("Request {} of {} has provided incorrect sca challenge", ctx.getRequestId(), ctx.getSagaId());
                    ctx.setWrongAuthCredentials(true);
                }
        );
    }

    @Service
    public static class Extractor extends PathHeadersBodyMapperTemplate<
                                                                               Xs2aPisContext,
                                                                               Xs2aAuthorizedPaymentParameters,
                                                                               Xs2aStandardHeaders,
                                                                               ProvideScaChallengeResultBody,
                                                                               TransactionAuthorisation> {

        public Extractor(
                DtoMapper<Xs2aContext, ProvideScaChallengeResultBody> toValidatableBody,
                DtoMapper<ProvideScaChallengeResultBody, TransactionAuthorisation> toBody,
                DtoMapper<Xs2aContext, Xs2aStandardHeaders> toHeaders,
                DtoMapper<Xs2aPisContext, Xs2aAuthorizedPaymentParameters> toParameters) {
            super(toValidatableBody, toBody, toHeaders, toParameters);
        }
    }
}
