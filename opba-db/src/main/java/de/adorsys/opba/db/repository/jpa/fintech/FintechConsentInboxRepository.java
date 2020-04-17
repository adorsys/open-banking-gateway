package de.adorsys.opba.db.repository.jpa.fintech;

import de.adorsys.opba.db.domain.entity.fintech.FintechPsuAspspPrvKeyInbox;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FintechConsentInboxRepository extends CrudRepository<FintechPsuAspspPrvKeyInbox, UUID> {
}
