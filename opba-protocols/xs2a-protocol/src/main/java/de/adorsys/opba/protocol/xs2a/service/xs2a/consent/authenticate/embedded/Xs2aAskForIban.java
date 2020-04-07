package de.adorsys.opba.protocol.xs2a.service.xs2a.consent.authenticate.embedded;

import de.adorsys.opba.protocol.xs2a.service.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.service.xs2a.RedirectExecutor;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.CONTEXT;

@Service("xs2aAskForIban")
@RequiredArgsConstructor
public class Xs2aAskForIban extends ValidatedExecution<Xs2aContext> {

    private final RuntimeService runtimeService;
    private final RedirectExecutor redirectExecutor;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        redirectExecutor.redirect(execution, context, redir -> redir.getParameters().getProvidePsuIban());
    }

    protected void doMockedExecution(DelegateExecution execution, Xs2aContext context) {
        context.setConsentId("MOCK-" + UUID.randomUUID().toString());
        execution.setVariable(CONTEXT, context);
        runtimeService.trigger(execution.getId());
    }
}
