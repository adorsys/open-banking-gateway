package de.adorsys.opba.protocol.facade.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.context.ServiceContext;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceContextProvider {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    private final ServiceSessionRepository serviceSessions;

    @Transactional
    @SneakyThrows
    public <T extends FacadeServiceableGetter> ServiceContext<T> provide(T request) {
        ServiceSession session;

        if (null != request.getFacadeServiceable().getServiceSessionId()) {
            UUID sessionId = UUID.fromString(request.getFacadeServiceable().getServiceSessionId());
            session = serviceSessions.findById(sessionId).
                    orElseThrow(() -> new IllegalStateException("No service session " + sessionId));
        } else {
            session = new ServiceSession();
            session.setContext(MAPPER.writeValueAsString(request));
            session = serviceSessions.save(session);
        }

        ServiceContext<T> context = ServiceContext.<T>builder()
                .facadeServiceable(request.getFacadeServiceable())
                .request(request)
                .build();

        context.getFacadeServiceable().setServiceSessionId(session.getId().toString());
        return context;
    }
}
