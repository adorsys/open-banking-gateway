package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.protocol.api.authorization.DenyAuthorization;
import de.adorsys.opba.protocol.api.dto.request.authorization.DenyAuthorizationRequest;
import de.adorsys.opba.protocol.api.dto.result.body.DenyAuthBody;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.DENY_AUTHORIZATION;
import static de.adorsys.opba.protocol.facade.services.context.NoRedirectCodeValidationServiceContextProvider.NO_REDIRECT_CODE_VALIDATION;

@Service
public class DenyAuthorizationService extends FacadeService<DenyAuthorizationRequest, DenyAuthBody, DenyAuthorization> {

    public DenyAuthorizationService(
            Map<String, ? extends DenyAuthorization> actionProviders,
            ProtocolSelector selector,
            @Qualifier(NO_REDIRECT_CODE_VALIDATION) ServiceContextProvider provider,
            ProtocolResultHandler handler) {
        super(DENY_AUTHORIZATION, actionProviders, selector, provider, handler);
    }
}
