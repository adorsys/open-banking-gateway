package de.adorsys.opba.fintech.impl.database.repositories;

import de.adorsys.opba.fintech.impl.database.entities.SessionEntity;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SessionRepository extends CrudRepository<SessionEntity, Long> {
    Optional<SessionEntity> findBySessionCookieValue(String sessionCookieValue);
    Iterable<SessionEntity> findByUserEntity(UserEntity userEntity);
}
