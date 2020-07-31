package de.adorsys.opba.db.repository.jpa.fintech;

import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechPubKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface FintechOnlyPubKeyRepository extends CrudRepository<FintechPubKey, UUID> {

    @Query("SELECT prv.pubKey FROM FintechPrvKey prv WHERE prv.fintech = :fintech")
    Collection<FintechPubKey> findByFintech(@Param("fintech") Fintech fintech);
}
