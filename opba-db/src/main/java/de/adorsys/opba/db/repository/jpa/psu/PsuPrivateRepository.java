package de.adorsys.opba.db.repository.jpa.psu;

import de.adorsys.opba.db.domain.entity.psu.PsuConsent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PsuPrivateRepository extends CrudRepository<PsuConsent, UUID> {
}
