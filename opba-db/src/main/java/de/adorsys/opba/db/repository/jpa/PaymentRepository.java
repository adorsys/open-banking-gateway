package de.adorsys.opba.db.repository.jpa;

import de.adorsys.opba.db.domain.entity.Payment;
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
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByServiceSessionIdOrderByModifiedAtDesc(UUID serviceSessionId);

    Collection<Payment> findByPsu(Psu owner);

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.confirmed = true WHERE p.serviceSession.id = :serviceSessionId")
    int setConfirmed(@Param("serviceSessionId") UUID serviceSessionId);
}
