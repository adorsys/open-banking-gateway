package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded;

import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.bpmnshared.service.context.ContextUtil;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded.ProvideScaChallengeResultBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.opba.protocol.xs2a.util.logresolver.Xs2aLogResolver;
import de.adorsys.xs2a.adapter.api.AccountInformationService;
import de.adorsys.xs2a.adapter.api.Response;
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
@Service("xs2aAuthenticateConsentWithScaChallenge")
@RequiredArgsConstructor
@SuppressWarnings("CPD-START")
public class Xs2aAisAuthenticateConsentWithScaChallenge extends ValidatedExecution<Xs2aContext> {

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

        ValidatedPathHeadersBody<Xs2aAuthorizedConsentParameters, Xs2aStandardHeaders, TransactionAuthorisation> params =
                extractor.forExecution(context);

        errorSink.handlePossibleAuthorizationError(
                () -> aisAuthorizeWithSca(execution, params),
                ex -> aisOnWrongSca(execution)
        );
    }

    private void aisAuthorizeWithSca(
            DelegateExecution execution,
            ValidatedPathHeadersBody<Xs2aAuthorizedConsentParameters, Xs2aStandardHeaders, TransactionAuthorisation> params) {

        logResolver.log("updateConsentsPsuData with parameters: {}", params.getPath(), params.getHeaders(), params.getBody());

        Response<ScaStatusResponse> authResponse = ais.updateConsentsPsuData(
                params.getPath().getConsentId(),
                params.getPath().getAuthorizationId(),
                params.getHeaders().toHeaders(),
                params.getPath().toParameters(),
                params.getBody()
        );

        logResolver.log("updateConsentsPsuData response: {}", authResponse);

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    ctx.setLastScaChallenge(null); // eagerly destroy SCA challenge, albeit it is not persisted
                    ctx.setWrongAuthCredentials(false);
                    ctx.setScaStatus(authResponse.getBody().getScaStatus().toString());
                }
        );
    }

    private void aisOnWrongSca(DelegateExecution execution) {
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
                                Xs2aContext,
                                Xs2aAuthorizedConsentParameters,
                                Xs2aStandardHeaders,
                                ProvideScaChallengeResultBody,
                                TransactionAuthorisation> {

        public Extractor(
                DtoMapper<Xs2aContext, ProvideScaChallengeResultBody> toValidatableBody,
                DtoMapper<ProvideScaChallengeResultBody, TransactionAuthorisation> toBody,
                DtoMapper<Xs2aContext, Xs2aStandardHeaders> toHeaders,
                DtoMapper<Xs2aContext, Xs2aAuthorizedConsentParameters> toParameters) {
            super(toValidatableBody, toBody, toHeaders, toParameters);
        }
    }
}
