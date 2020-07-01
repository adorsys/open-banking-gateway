package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.Consent;
import de.adorsys.opba.db.domain.entity.psu.Psu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {

    List<Consent> findByServiceSessionIdOrderByModifiedAtDesc(UUID serviceSessionId);
    Collection<Consent> findByPsu(Psu owner);

    @Modifying
    @Transactional
    @Query("UPDATE Consent c SET c.confirmed = true WHERE c.serviceSession.id = :serviceSessionId")
    int setConfirmed(@Param("serviceSessionId") UUID serviceSessionId);
}
