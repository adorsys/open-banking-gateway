package de.adorsys.opba.db.repository.jpa.fintech;

import de.adorsys.opba.db.domain.entity.fintech.FintechPrivate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FintechPrivateRepository extends CrudRepository<FintechPrivate, UUID> {
}
