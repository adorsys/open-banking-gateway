package de.adorsys.opba.protocol.facade.services.fromaspsp;

import de.adorsys.opba.protocol.api.FromAspspRedirect;
import de.adorsys.opba.protocol.api.dto.request.authorization.fromaspsp.FromAspspRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ServiceContextProvider;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.db.domain.entity.ProtocolAction.FROM_ASPSP_REDIRECT;

@Service
public class FromAspspRedirectHandler extends FacadeService<FromAspspRequest, UpdateAuthBody, FromAspspRedirect> {

    public FromAspspRedirectHandler(
        Map<String, ? extends FromAspspRedirect> actionProviders,
        ProtocolSelector selector,
        ServiceContextProvider provider,
        ProtocolResultHandler handler) {
        super(FROM_ASPSP_REDIRECT, actionProviders, selector, provider, handler);
    }
}
