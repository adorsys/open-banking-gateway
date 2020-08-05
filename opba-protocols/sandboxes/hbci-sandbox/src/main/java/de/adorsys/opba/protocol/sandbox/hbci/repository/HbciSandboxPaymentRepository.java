package de.adorsys.opba.protocol.sandbox.hbci.repository;

import de.adorsys.opba.protocol.sandbox.hbci.domain.HbciSandboxPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HbciSandboxPaymentRepository extends JpaRepository<HbciSandboxPayment, Long> {

    Optional<HbciSandboxPayment> findByOwnerLoginAndOrderReference(String ownerLogin, String orderReference);
    List<HbciSandboxPayment> findByOwnerLoginOrderByCreatedAtDesc(String ownerLogin);
}
