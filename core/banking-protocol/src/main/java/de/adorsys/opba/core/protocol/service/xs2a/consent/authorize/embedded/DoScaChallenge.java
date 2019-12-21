package de.adorsys.opba.core.protocol.service.xs2a.consent.authorize.embedded;

import de.adorsys.opba.core.protocol.service.ValidatedExecution;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.AccountInformationService;
import de.adorsys.xs2a.adapter.service.model.PsuData;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("xs2aDoScaChallenge")
@RequiredArgsConstructor
public class DoScaChallenge  extends ValidatedExecution<Xs2aContext> {

    private final AccountInformationService ais;

    @Override
    protected void doRealExecution(DelegateExecution execution, Xs2aContext context) {
        ais.updateConsentsPsuData(
                context.getConsentId(),
                context.getAuthorizationId(),
                context.toHeaders(),
                authentication()
        );
    }

    private UpdatePsuAuthentication authentication() {
        UpdatePsuAuthentication psuAuthentication = new UpdatePsuAuthentication();
        PsuData data = new PsuData();
        data.setPassword("12345");
        psuAuthentication.setPsuData(data);
        return psuAuthentication;
    }
}
