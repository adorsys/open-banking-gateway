package de.adorsys.opba.protocol.facade.services.context;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.facade.services.InternalContext;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceContextProvider {

    @Transactional
    @SneakyThrows
    <REQUEST extends FacadeServiceableGetter, ACTION> InternalContext<REQUEST, ACTION> provide(REQUEST request);

    <REQUEST extends FacadeServiceableGetter, ACTIONS> ServiceContext<REQUEST> provideRequestScoped(REQUEST request, InternalContext<REQUEST, ACTIONS> ctx);
}
