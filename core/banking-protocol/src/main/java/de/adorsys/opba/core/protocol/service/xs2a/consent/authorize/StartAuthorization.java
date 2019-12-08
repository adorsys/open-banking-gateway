package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize;

import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.StartScaProcessResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.service.ResponseHeaders.ASPSP_SCA_APPROACH;

@Service("xs2aStartAuthorization")
@RequiredArgsConstructor
public class StartAuthorization implements JavaDelegate {

    private final AccountInformationService ais;

    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) {
        Xs2aContext context = delegateExecution.getVariable(CONTEXT, Xs2aContext.class);
        context.setRedirectUriOk(context.getRedirectUriOk() + delegateExecution.getProcessInstanceId());

        Response<StartScaProcessResponse> scaStart = ais.startConsentAuthorisation(
                context.getConsentId(),
                context.toHeaders()
        );

        context.setAspspScaApproach(scaStart.getHeaders().getHeader(ASPSP_SCA_APPROACH));
        context.setAuthorizationId(scaStart.getBody().getAuthorisationId());
        context.setStartScaProcessResponse(scaStart.getBody());
        delegateExecution.setVariable(CONTEXT, context);
    }
}
