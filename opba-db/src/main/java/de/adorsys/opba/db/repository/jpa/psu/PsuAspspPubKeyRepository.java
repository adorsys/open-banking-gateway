package de.adorsys.opba.db.repository.jpa.psu;

import de.adorsys.opba.db.domain.entity.psu.PsuAspspPubKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PsuAspspPubKeyRepository extends CrudRepository<PsuAspspPubKey, UUID> {
}
