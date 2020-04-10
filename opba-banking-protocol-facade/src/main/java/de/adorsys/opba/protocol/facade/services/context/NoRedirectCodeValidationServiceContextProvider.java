package de.adorsys.opba.protocol.facade.services.context;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import de.adorsys.opba.db.repository.jpa.AuthenticationSessionRepository;
import de.adorsys.opba.db.repository.jpa.ServiceSessionRepository;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.api.services.SecretKeyOperations;
import org.springframework.stereotype.Service;

@Service(NoRedirectCodeValidationServiceContextProvider.NO_REDIRECT_CODE_VALIDATION)
public class NoRedirectCodeValidationServiceContextProvider extends ServiceContextProviderForFintech {

    public static final String NO_REDIRECT_CODE_VALIDATION = "NO_REDIRECT_CODE_VALIDATION_CONTEXT_PROVIDER";

    public NoRedirectCodeValidationServiceContextProvider(AuthenticationSessionRepository authSessions,
                                                          ServiceSessionRepository serviceSessions,
                                                          SecretKeyOperations secretKeyOperations) {
        super(authSessions, serviceSessions, secretKeyOperations);
    }

    protected <T extends FacadeServiceableGetter> void validateRedirectCode(T request, AuthSession session) {
        // NOP
    }
}
