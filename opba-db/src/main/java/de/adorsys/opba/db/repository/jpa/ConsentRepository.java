package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
}
