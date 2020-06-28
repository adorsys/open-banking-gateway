package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.nonauthorized;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service("authenticatedAwait")
public class AuthenticatedAwaitForRequest implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // NOP
    }
}
