package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.service.ContextUtil;
import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.pis.Xs2aPisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedPaymentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.authenticate.embedded.SelectScaChallengeBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.PaymentInitiationService;
import de.adorsys.xs2a.adapter.service.RequestParams;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.SelectPsuAuthenticationMethod;
import de.adorsys.xs2a.adapter.service.model.SelectPsuAuthenticationMethodResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Send the selected SCA method by the user to the ASPSP. The challenge is typically provided by
 * {@link de.adorsys.opba.protocol.api.authorization.UpdateAuthorization}.
 */
@Service("xs2aPisReportSelectedScaMethod")
@RequiredArgsConstructor
public class Xs2aPisReportSelectedScaMethod extends ValidatedExecution<Xs2aPisContext> {

    private final Extractor extractor;
    private final Xs2aValidator validator;
    private final PaymentInitiationService pis;

    @Override
    protected void doValidate(DelegateExecution execution, Xs2aPisContext context) {
        validator.validate(execution, extractor.forValidation(context));
    }

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aPisContext context) {
        ValidatedPathHeadersBody<Xs2aAuthorizedPaymentParameters, Xs2aStandardHeaders, SelectPsuAuthenticationMethod> params = extractor.forExecution(context);

        Response<SelectPsuAuthenticationMethodResponse> authResponse = pis.updatePaymentPsuData(
                params.getPath().getPaymentType().getValue(),
                params.getPath().getPaymentProduct(),
                params.getPath().getPaymentId(),
                params.getPath().getAuthorizationId(),
                params.getHeaders().toHeaders(),
                RequestParams.empty(),
                params.getBody()
        );

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> ctx.setScaSelected(authResponse.getBody().getChosenScaMethod())
        );
    }

    @Service
    public static class Extractor extends PathHeadersBodyMapperTemplate<Xs2aPisContext,
                                                                               Xs2aAuthorizedPaymentParameters,
                                                                               Xs2aStandardHeaders,
                                                                               SelectScaChallengeBody,
                                                                               SelectPsuAuthenticationMethod> {

        public Extractor(
                DtoMapper<Xs2aContext, SelectScaChallengeBody> toValidatableBody,
                DtoMapper<SelectScaChallengeBody, SelectPsuAuthenticationMethod> toBody,
                DtoMapper<Xs2aContext, Xs2aStandardHeaders> toHeaders,
                DtoMapper<Xs2aPisContext, Xs2aAuthorizedPaymentParameters> toParameters) {
            super(toValidatableBody, toBody, toHeaders, toParameters);
        }
    }
}
