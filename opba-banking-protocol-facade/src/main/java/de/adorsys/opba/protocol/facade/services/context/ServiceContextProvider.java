package de.adorsys.opba.protocol.facade.services.context;

import de.adorsys.opba.protocol.api.common.ProtocolAction;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceContextProvider {

    @Transactional
    @SneakyThrows
    <T extends FacadeServiceableGetter> ServiceContext<T> provide(T request, ProtocolAction protocolAction);
}
