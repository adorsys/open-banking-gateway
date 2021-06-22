package de.adorsys.opba.protocol.facade.services.ais;

import de.adorsys.opba.protocol.api.ais.DeleteConsent;
import de.adorsys.opba.protocol.api.dto.request.authorization.DeleteConsentRequest;
import de.adorsys.opba.protocol.api.dto.result.body.DeleteConsentBody;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.DELETE_CONSENT;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;

@Service
public class DeleteConsentService extends FacadeService<DeleteConsentRequest, DeleteConsentBody, DeleteConsent> {

    public DeleteConsentService(
        Map<String, ? extends DeleteConsent> actionProviders,
        ProtocolSelector selector,
        @Qualifier(FINTECH_CONTEXT_PROVIDER) ServiceContextProvider provider,
        ProtocolResultHandler handler,
        TransactionTemplate txTemplate) {
        super(DELETE_CONSENT, actionProviders, selector, provider, handler, txTemplate);
    }
}
