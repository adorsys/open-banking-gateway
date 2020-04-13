package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.sessions.AuthSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorizationSessionRepository extends CrudRepository<AuthSession, UUID> {

    Optional<AuthSession> findByParentId(UUID serviceSessionId);
}
