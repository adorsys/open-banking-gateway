package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.protocol.api.authorization.UpdateAuthorization;
import de.adorsys.opba.protocol.api.dto.request.authorization.AuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.UPDATE_AUTHORIZATION;

@Async
@Service
public class UpdateAuthorizationService extends FacadeService<AuthorizationRequest, UpdateAuthBody, UpdateAuthorization> {

    public UpdateAuthorizationService(
        Map<String, ? extends UpdateAuthorization> actionProviders,
        ProtocolSelector selector,
        ServiceContextProvider provider,
        ProtocolResultHandler handler) {
        super(UPDATE_AUTHORIZATION, actionProviders, selector, provider, handler);
    }
}
