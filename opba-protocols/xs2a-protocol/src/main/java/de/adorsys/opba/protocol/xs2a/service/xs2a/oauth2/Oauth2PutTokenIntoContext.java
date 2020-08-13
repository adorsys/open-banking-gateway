package de.adorsys.opba.protocol.xs2a.service.xs2a.oauth2;

import de.adorsys.opba.protocol.bpmnshared.service.exec.ValidatedExecution;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("oauth2PutTokenIntoContext")
@RequiredArgsConstructor
public class Oauth2PutTokenIntoContext extends ValidatedExecution<Xs2aContext> {

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
    }
}
