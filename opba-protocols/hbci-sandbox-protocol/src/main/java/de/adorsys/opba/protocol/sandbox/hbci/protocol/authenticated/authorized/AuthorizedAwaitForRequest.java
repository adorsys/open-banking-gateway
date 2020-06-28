package de.adorsys.opba.protocol.sandbox.hbci.protocol.authenticated.authorized;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service("authorizedAwait")
public class AuthorizedAwaitForRequest implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // NOP
    }
}
