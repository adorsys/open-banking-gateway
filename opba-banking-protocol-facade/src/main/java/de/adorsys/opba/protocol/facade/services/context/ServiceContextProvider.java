package de.adorsys.opba.protocol.facade.services.context;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.facade.services.InternalContext;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceContextProvider {

    @Transactional
    @SneakyThrows
    <T extends FacadeServiceableGetter> InternalContext<T> provide(T request);

    <T extends FacadeServiceableGetter> ServiceContext<T> provideRequestScoped(T request, InternalContext<T> ctx);
}
