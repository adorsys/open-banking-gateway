package de.adorsys.opba.db.repository.jpa.fintech;

import de.adorsys.opba.db.domain.entity.fintech.FintechPrvKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FintechOnlyPrvKeyRepository extends CrudRepository<FintechPrvKey, UUID> {

    Optional<FintechPrvKey> findByIdAndFintechId(UUID id, long fintechId);
}
