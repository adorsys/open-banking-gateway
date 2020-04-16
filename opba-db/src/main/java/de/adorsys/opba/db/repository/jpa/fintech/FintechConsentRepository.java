package de.adorsys.opba.db.repository.jpa.fintech;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.fintech.Fintech;
import de.adorsys.opba.db.domain.entity.fintech.FintechConsent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FintechConsentRepository extends CrudRepository<FintechConsent, UUID> {

    Optional<FintechConsent> findByFintechAndConsent(Fintech fintech, Consent consent);
    Optional<FintechConsent> findByFintechIdAndConsentId(Long fintechId, UUID consentId);
}
