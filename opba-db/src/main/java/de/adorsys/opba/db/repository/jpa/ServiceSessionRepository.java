package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.sessions.ServiceSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServiceSessionRepository extends CrudRepository<ServiceSession, UUID> {
}
