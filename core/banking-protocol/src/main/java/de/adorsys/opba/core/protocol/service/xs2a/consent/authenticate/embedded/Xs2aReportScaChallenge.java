package de.adorsys.opba.core.protocol.service.xs2a.consent.authenticate.embedded;

import de.adorsys.opba.core.protocol.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.core.protocol.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.core.protocol.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.core.protocol.service.xs2a.dto.consent.authenticate.embedded.ProvideScaChallengeResultBody;
import de.adorsys.opba.core.protocol.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.ScaStatusResponse;
import de.adorsys.xs2a.adapter.service.model.TransactionAuthorisation;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aReportScaChallenge")
@RequiredArgsConstructor
public class Xs2aReportScaChallenge extends ValidatedExecution<Xs2aContext> {

    private final Extractor extractor;
    private final Xs2aValidator validator;
    private final RuntimeService runtimeService;
    private final AccountInformationService ais;

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aContext context) {
        validator.validate(execution, extractor.forValidation(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        ValidatedPathHeadersBody<Xs2aAuthorizedConsentParameters, Xs2aStandardHeaders, TransactionAuthorisation> params =
                extractor.forExecution(context);

        Response<ScaStatusResponse> authResponse = ais.updateConsentsPsuData(
                params.getPath().getConsentId(),
                params.getPath().getAuthorizationId(),
                params.getHeaders().toHeaders(),
                params.getBody()
        );

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> ctx.setScaStatus(authResponse.getBody().getScaStatus().getValue())
        );
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        runtimeService.trigger(execution.getId());
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
