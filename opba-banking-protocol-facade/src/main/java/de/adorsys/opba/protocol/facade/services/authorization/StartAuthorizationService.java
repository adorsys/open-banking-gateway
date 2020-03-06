package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.protocol.api.authorization.StartAuthorization;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.START_AUTHORIZATION;

@Service
public class StartAuthorizationService extends FacadeService<AuthorizationRequest, UpdateAuthBody, StartAuthorization> {

    public StartAuthorizationService(
        Map<String, ? extends StartAuthorization> actionProviders,
        ProtocolSelector selector,
        ServiceContextProvider provider,
        ProtocolResultHandler handler) {
        super(START_AUTHORIZATION, actionProviders, selector, provider, handler);
    }
}
