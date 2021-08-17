package de.adorsys.opba.protocol.xs2a.service.xs2a.authenticate.decoupled;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.config.protocol.ProtocolUrlsConfiguration;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.Xs2aRedirectExecutor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * Asks PSU to proceed SCA with his external device by redirecting him to the waiting page. Suspends process to wait for users' actions.
 */
@Service("xs2aAskForDecoupledInitiation")
@RequiredArgsConstructor
public class Xs2aAskForDecoupledFinalization extends ValidatedExecution<Xs2aContext> {

    private final Xs2aRedirectExecutor redirectExecutor;
    private final ProtocolUrlsConfiguration urls;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        ProtocolUrlsConfiguration.UrlSet urlSet = ProtocolAction.SINGLE_PAYMENT.equals(context.getAction())
                                                          ? urls.getPis() : urls.getAis();
        String redirectUrl = urlSet.getParameters().getWaitDecoupledSca();

        redirectExecutor.redirect(
                execution,
                context,
                redirectUrl,
                null,
                redirect -> redirect.doNotRemoveKey(true).build()
        );
    }
}
