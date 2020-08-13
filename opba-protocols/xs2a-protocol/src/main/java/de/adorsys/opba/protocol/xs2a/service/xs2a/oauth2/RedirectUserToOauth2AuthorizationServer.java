package de.adorsys.opba.protocol.xs2a.service.xs2a.oauth2;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

/**
 * We expect PKCE authorization from Xs2a adapter (no state, etc.)
 */
@Service("redirectUserToOauth2AuthorizationServer")
@RequiredArgsConstructor
public class RedirectUserToOauth2AuthorizationServer extends ValidatedExecution<Xs2aContext> {

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
    }
}
