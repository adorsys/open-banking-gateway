package de.adorsys.opba.protocol.facade.services.context;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthorizationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.facade.services.RequestScopedProvider;
import de.adorsys.opba.protocol.facade.services.SecretKeySerde;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service(ServiceContextProviderForAspsp.ASPSP_CONTEXT_PROVIDER)
public class ServiceContextProviderForAspsp extends ServiceContextProviderForFintech {

    public static final String ASPSP_CONTEXT_PROVIDER = "ASPSP_CONTEXT_PROVIDER";

    public ServiceContextProviderForAspsp(AuthorizationSessionRepository authSessions,
                                          SecretKeySerde serde,
                                          ServiceSessionRepository serviceSessions,
                                          RequestScopedProvider requestScopedProvider) {
        super(authSessions, requestScopedProvider, serde, serviceSessions);
    }

    protected <T extends FacadeServiceableGetter> void validateRedirectCode(T request, AuthSession session) {
        if (!Objects.equals(session.getAspspRedirectCode(), request.getFacadeServiceable().getRedirectCode())) {
            throw new IllegalArgumentException("Wrong redirect code");
        }
    }
}
