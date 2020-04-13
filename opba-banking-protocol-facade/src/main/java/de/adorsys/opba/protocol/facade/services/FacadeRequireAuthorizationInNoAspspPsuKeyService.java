package de.adorsys.opba.protocol.facade.services;

import com.google.common.base.Strings;
import de.adorsys.opba.db.domain.entity.ProtocolAction;
import de.adorsys.opba.protocol.api.Action;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.dto.result.body.ResultBody;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import de.adorsys.opba.protocol.facade.result.InternalAuthorizationRequiredResult;
import de.adorsys.opba.protocol.facade.services.context.ServiceContextProvider;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FacadeRequireAuthorizationInNoAspspPsuKeyService<I extends FacadeServiceableGetter, O extends ResultBody, A extends Action<I, O>> extends FacadeService<I, O, A> {

    public FacadeRequireAuthorizationInNoAspspPsuKeyService(
            ProtocolAction action,
            Map<String, ? extends A> actionProviders,
            ProtocolSelector selector,
            ServiceContextProvider provider,
            ProtocolResultHandler handler
    ) {
        super(action, actionProviders, selector, provider, handler);
    }

    @Override
    public CompletableFuture<Result<O>> execute(A protocol, ServiceContext<I> ctx) {
        if (Strings.isNullOrEmpty(ctx.getRequest().getFacadeServiceable().getPsuAspspKeyId())) {
            return CompletableFuture.completedFuture(
                    new InternalAuthorizationRequiredResult<>()
            );
        }

        return protocol.execute(ctx);
    }
}
