package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {

    Optional<Consent> findByServiceSessionId(UUID serviceSessionId);
    Collection<Consent> findByPsu(Psu owner);
    Optional<Consent> findByServiceSessionAuthSessionId(UUID authSessionId);
}
