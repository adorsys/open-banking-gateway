package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.protocol.api.authorization.FromAspspRedirect;
import de.adorsys.opba.protocol.api.dto.request.authorization.fromaspsp.FromAspspRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.FROM_ASPSP_REDIRECT;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForAspsp.ASPSP_CONTEXT_PROVIDER;

/**
 * Action that is executed after user is redirected back from ASPSP to restore consent authorization session.
 */
@Service
public class FromAspspRedirectHandler extends FacadeService<FromAspspRequest, UpdateAuthBody, FromAspspRedirect> {

    public FromAspspRedirectHandler(
        Map<String, ? extends FromAspspRedirect> actionProviders,
        ProtocolSelector selector,
        @Qualifier(ASPSP_CONTEXT_PROVIDER) ServiceContextProvider provider,
        ProtocolResultHandler handler,
        TransactionTemplate txTemplate) {
        super(FROM_ASPSP_REDIRECT, actionProviders, selector, provider, handler, txTemplate);
    }
}
