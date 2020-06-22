package de.adorsys.opba.protocol.facade.services.context;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.facade.services.InternalContext;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceContextProvider {

    @Transactional
    @SneakyThrows
    <T extends FacadeServiceableGetter, A> InternalContext<T, A> provide(T request);

    <T extends FacadeServiceableGetter, A> ServiceContext<T> provideRequestScoped(T request, InternalContext<T, A> ctx);
}
