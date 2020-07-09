package de.adorsys.opba.protocol.xs2a.service.xs2a.consent.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.domain.dto.forms.ScaMethod;
import de.adorsys.opba.protocol.xs2a.service.mapper.PathHeadersBodyMapperTemplate;
import de.adorsys.opba.protocol.xs2a.service.ContextUtil;
import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.dto.ValidatedPathHeadersBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aAuthorizedConsentParameters;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent.authenticate.embedded.ProvidePsuPasswordBody;
import de.adorsys.opba.protocol.xs2a.service.xs2a.validation.Xs2aValidator;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service("xs2aAuthenticateUserConsent")
@RequiredArgsConstructor
public class Xs2aAuthenticateUserConsent extends ValidatedExecution<Xs2aContext> {

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
        ValidatedPathHeadersBody<Xs2aAuthorizedConsentParameters, Xs2aStandardHeaders, UpdatePsuAuthentication> params =
                extractor.forExecution(context);

        Response<UpdatePsuAuthenticationResponse> authResponse = ais.updateConsentsPsuData(
                params.getPath().getConsentId(),
                params.getPath().getAuthorizationId(),
                params.getHeaders().toHeaders(),
                params.getBody()
        );

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    setScaAvailableMethodsIfCanBeChosen(authResponse, ctx);
                    ctx.setScaSelected(authResponse.getBody().getChosenScaMethod());
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

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        runtimeService.trigger(execution.getId());
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
