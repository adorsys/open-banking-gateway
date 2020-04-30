package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.protocol.api.authorization.UpdateAuthorization;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.UPDATE_AUTHORIZATION;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;

@Service
public class UpdateAuthorizationService extends FacadeService<AuthorizationRequest, UpdateAuthBody, UpdateAuthorization> {

    public UpdateAuthorizationService(
        Map<String, ? extends UpdateAuthorization> actionProviders,
        ProtocolSelector selector,
        @Qualifier(FINTECH_CONTEXT_PROVIDER) ServiceContextProvider provider,
        ProtocolResultHandler handler,
        TransactionTemplate txTemplate) {
        super(UPDATE_AUTHORIZATION, actionProviders, selector, provider, handler, txTemplate);
    }
}
