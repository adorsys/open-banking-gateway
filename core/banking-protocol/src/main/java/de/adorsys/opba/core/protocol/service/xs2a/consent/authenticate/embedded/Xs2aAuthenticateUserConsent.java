package de.adorsys.opba.core.protocol.service.xs2a.consent.authenticate.embedded;

import de.adorsys.opba.core.protocol.domain.dto.forms.ScaMethod;
import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.Xs2aStandardHeaders;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.core.protocol.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;

@Service("xs2aAuthenticateUserConsent")
@RequiredArgsConstructor
public class Xs2aAuthenticateUserConsent extends ValidatedExecution<Xs2aContext> {

    private final Xs2aAuthenticateUserConsent.FromCtx toBody;
    private final Xs2aStandardHeaders.FromCtx toHeaders;
    private final RuntimeService runtimeService;
    private final AccountInformationService ais;

    // TODO validation projection
    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Response<UpdatePsuAuthenticationResponse> authResponse = ais.updateConsentsPsuData(
                context.getConsentId(),
                context.getAuthorizationId(),
                toHeaders.map(context).toHeaders(),
                toBody.map(context)
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

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx {

        @Mapping(target = "psuData.password", source = "psuPassword")
        UpdatePsuAuthentication map(Xs2aContext ctx);
    }
}
