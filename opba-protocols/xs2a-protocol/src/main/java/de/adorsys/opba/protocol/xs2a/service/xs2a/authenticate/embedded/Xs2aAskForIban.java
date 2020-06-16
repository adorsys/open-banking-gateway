package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.embedded;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aRedirectExecutor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;

/**
 * Special component to return PSU back to IBAN (dedicated consent) input page after the ASPSP replies that
 * the IBAN list is incorrect (after {@link de.adorsys.opba.protocol.xs2a.service.xs2a.consent.CreateConsentErrorSink} has captured an error).
 * Suspends the process to wait for user input.
 */
@Service("xs2aAskForIban")
@RequiredArgsConstructor
public class Xs2aAskForIban extends ValidatedExecution<Xs2aContext> {

    private final RuntimeService runtimeService;
    private final Xs2aRedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        redirectExecutor.redirect(execution, context, urls -> urls.getAis().getParameters().getProvidePsuIban());
    }

    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        context.setConsentId("MOCK-" + UUID.randomUUID().toString());
        execution.setVariable(CONTEXT, context);
        runtimeService.trigger(execution.getId());
    }
}
