package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.consentapi.model.generated.InlineResponse200;
import de.adorsys.opba.protocol.api.UpdateAuthorization;
import de.adorsys.opba.protocol.api.dto.request.authentication.AuthorizationRequest;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ResultHandler;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.UPDATE_AUTHORIZATION;

@Service
public class UpdateAuthorizationService extends FacadeService<AuthorizationRequest, InlineResponse200, UpdateAuthorization> {

    public UpdateAuthorizationService(
            Map<String, ? extends UpdateAuthorization> actionProviders,
            ProtocolSelector selector,
            ServiceContextProvider provider,
            ResultHandler handler) {
        super(UPDATE_AUTHORIZATION, actionProviders, selector, provider, handler);
    }
}
