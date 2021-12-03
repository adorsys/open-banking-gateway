package de.adorsys.opba.protocol.facade.services.authorization;

import de.adorsys.opba.protocol.api.authorization.OnLogin;
import de.adorsys.opba.protocol.api.dto.request.authorization.OnLoginRequest;
import de.adorsys.opba.protocol.api.dto.result.body.UpdateAuthBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.facade.dto.result.torest.FacadeResult;
import de.adorsys.opba.protocol.facade.services.FacadeOptionalService;
import de.adorsys.opba.protocol.facade.services.ProtocolResultHandler;
import de.adorsys.opba.protocol.facade.services.ProtocolSelector;
import de.adorsys.opba.protocol.facade.services.ProtocolWithCtx;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.adorsys.opba.protocol.api.common.ProtocolAction.ON_LOGIN;
import static de.adorsys.opba.protocol.facade.services.context.ServiceContextProviderForFintech.FINTECH_CONTEXT_PROVIDER;

/**
 * Action executed on PSU/Fintech user login into Consent UI (as a last login action in Facade scope besides checking credentials).
 */
@Service
public class OnLoginService extends FacadeOptionalService<OnLoginRequest, UpdateAuthBody, OnLogin> {

    public OnLoginService(
            Map<String, ? extends OnLogin> actionProviders,
            ProtocolSelector selector,
            @Qualifier(FINTECH_CONTEXT_PROVIDER) ServiceContextProvider provider,
            ProtocolResultHandler handler,
            TransactionTemplate txTemplate) {
        super(ON_LOGIN, actionProviders, selector, provider, handler, txTemplate);
    }

    @Override
    protected CompletableFuture<FacadeResult<UpdateAuthBody>> handleProtocolResult(
            OnLoginRequest onLoginRequest,
            ProtocolWithCtx<OnLogin, OnLoginRequest> protocolWithCtx,
            CompletableFuture<Result<UpdateAuthBody>> result
    ) {
        return result.thenCompose(it -> {
            if (null == it) {
                return CompletableFuture.completedFuture(null);
            }

            return super.handleProtocolResult(onLoginRequest, protocolWithCtx, result);
        });
    }
}
