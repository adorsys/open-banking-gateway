package de.adorsys.opba.db.repository.jpa.fintech;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FintechPsuAspspPrvKeyRepository extends CrudRepository<FintechPsuAspspPrvKey, UUID> {

    Optional<FintechPsuAspspPrvKey> findByFintechIdAndPsuIdAndAspspId(long fintechId, long psuId, long aspspId);
    void deleteByAspsp(Bank bank);
}
