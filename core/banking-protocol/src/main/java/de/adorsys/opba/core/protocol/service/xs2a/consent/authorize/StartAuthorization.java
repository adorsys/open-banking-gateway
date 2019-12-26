package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize;

import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.Response;
import de.adorsys.xs2a.adapter.service.model.StartScaProcessResponse;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.CONTEXT;
import static de.adorsys.xs2a.adapter.service.ResponseHeaders.ASPSP_SCA_APPROACH;

@Service("xs2aStartAuthorization")
@RequiredArgsConstructor
public class StartAuthorization extends ValidatedExecution<Xs2aContext> {

    private final AccountInformationService ais;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        context.setRedirectUriOk(context.getRedirectUriOk() + execution.getProcessInstanceId());

        Response<StartScaProcessResponse> scaStart = ais.startConsentAuthorisation(
                context.getConsentId(),
                context.toHeaders()
        );

        context.setAspspScaApproach(scaStart.getHeaders().getHeader(ASPSP_SCA_APPROACH));
        context.setAuthorizationId(scaStart.getBody().getAuthorisationId());
        context.setStartScaProcessResponse(scaStart.getBody());
        execution.setVariable(CONTEXT, context);
    }

    @Override
    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        // TODO: Read from database - preferred bank profile
        if (null == context.getAspspScaApproach()) {
            context.setAspspScaApproach("EMBEDDED");
        }
    }
}
