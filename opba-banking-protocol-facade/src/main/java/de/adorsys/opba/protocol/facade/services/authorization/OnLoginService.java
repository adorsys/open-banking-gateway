package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.protocol.api.authorization.OnLogin;
import de.adorsys.opba.protocol.api.dto.request.authorization.OnLoginRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.exceptions.NoProtocolRegisteredException;
import de.adorsys.opba.protocol.facade.services.FacadeService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.ON_LOGIN;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;

@Service
public class OnLoginService extends FacadeService<OnLoginRequest, UpdateAuthBody, OnLogin> {

    public OnLoginService(
            Map<String, ? extends OnLogin> actionProviders,
            ProtocolSelector selector,
            @Qualifier(FINTECH_CONTEXT_PROVIDER) ServiceContextProvider provider,
            ProtocolResultHandler handler,
            TransactionTemplate txTemplate) {
        super(ON_LOGIN, actionProviders, selector, provider, handler, txTemplate);
    }

    @Override
    public CompletableFuture<FacadeResult<UpdateAuthBody>> execute(OnLoginRequest onLoginRequest) {
        try {
            return super.execute(onLoginRequest);
        } catch (NoProtocolRegisteredException ex) {
            return CompletableFuture.completedFuture(null);
        }
    }
}
