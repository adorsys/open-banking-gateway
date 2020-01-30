package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServiceContextProvider {

    @Transactional
    public <T extends FacadeServiceableGetter> ServiceContext<T> provide(T request) {
        return null;
    }
}
