package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize.embedded;

import de.adorsys.opba.core.protocol.domain.dto.forms.ScaMethod;
import de.adorsys.opba.core.protocol.service.ContextUtil;
import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.PsuData;
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

    private final RuntimeService runtimeService;
    private final AccountInformationService ais;

    // TODO validation projection
    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        Response<UpdatePsuAuthenticationResponse> authResponse = ais.updateConsentsPsuData(
                context.getConsentId(),
                context.getAuthorizationId(),
                context.toHeaders(),
                authentication(context)
        );

        ContextUtil.getAndUpdateContext(
                execution,
                (Xs2aContext ctx) -> {
                    ctx.setAvailableSca(
                        authResponse.getBody().getScaMethods().stream()
                            .map(ScaMethod.FROM_AUTH::map)
                            .collect(Collectors.toList())
                    );
                    ctx.setScaSelected(authResponse.getBody().getChosenScaMethod());
                }
        );
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        runtimeService.trigger(execution.getId());
    }

    private UpdatePsuAuthentication authentication(Xs2aContext context) {
        UpdatePsuAuthentication psuAuthentication = new UpdatePsuAuthentication();
        PsuData data = new PsuData();
        data.setPassword(context.getPsuPassword());
        psuAuthentication.setPsuData(data);
        return psuAuthentication;
    }
}
