package de.adorsys.opba.protocol.facade.services.context;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.facade.services.InternalContext;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service context provider - provides facade structures to underlying protocols.
 */
public interface ServiceContextProvider {

    /**
     * Provides service context for a request.
     * @param request Request to provide context for
     * @param <REQUEST> Request class
     * @param <ACTION> Associated action
     * @return Facade structures to serve the request
     */
    @Transactional
    @SneakyThrows
    <REQUEST extends FacadeServiceableGetter, ACTION> InternalContext<REQUEST, ACTION> provide(REQUEST request);

    /**
     * Provide request-scoped services for the request.
     * @param request Request to provide services for
     * @param ctx Internal facade context associated with action
     * @param <REQUEST> Request class
     * @param <ACTIONS> Associated action
     * @return Facade structures to serve the request
     */
    <REQUEST extends FacadeServiceableGetter, ACTIONS> ServiceContext<REQUEST> provideRequestScoped(REQUEST request, InternalContext<REQUEST, ACTIONS> ctx);
}
