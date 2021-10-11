package de.adorsys.opba.db.repository.jpa.fintech;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKeyInbox;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FintechPsuAspspPrvKeyInboxRepository extends CrudRepository<FintechPsuAspspPrvKeyInbox, UUID> {

    Optional<FintechPsuAspspPrvKeyInbox> findByFintechIdAndPsuIdAndAspspId(long fintechId, long psuId, long aspspId);
    void deleteByAspsp(Bank bank);
}
