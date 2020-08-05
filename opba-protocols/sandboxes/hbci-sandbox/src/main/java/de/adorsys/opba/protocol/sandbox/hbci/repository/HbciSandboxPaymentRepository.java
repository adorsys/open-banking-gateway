package de.adorsys.opba.protocol.sandbox.hbci.repository;

import de.adorsys.opba.protocol.sandbox.hbci.domain.HbciSandboxPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HbciSandboxPaymentRepository extends JpaRepository<HbciSandboxPayment, Long> {

    List<HbciSandboxPayment> findByOwnerLoginOrderByCreatedAtDesc(String ownerLogin);
}
