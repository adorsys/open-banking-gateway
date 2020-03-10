package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.protocol.api.authorization.GetAuthorizationState;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.GET_AUTHORIZATION_STATE;

@Service
public class GetAuthorizationStateService extends FacadeService<AuthorizationRequest, AuthStateBody, GetAuthorizationState> {

    public GetAuthorizationStateService(
        Map<String, ? extends GetAuthorizationState> actionProviders,
        ProtocolSelector selector,
        ServiceContextProvider provider,
        ProtocolResultHandler handler) {
        super(GET_AUTHORIZATION_STATE, actionProviders, selector, provider, handler);
    }
}
