package de.adorsys.opba.db.repository.jpa.psu;

import de.adorsys.opba.db.domain.entity.psu.PsuAspspPrvKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PsuConsentRepository extends CrudRepository<PsuAspspPrvKey, UUID> {
}
