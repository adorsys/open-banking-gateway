package de.adorsys.opba.protocol.sandbox.hbci.repository;

import de.adorsys.multibanking.domain.PaymentStatus;
import de.adorsys.opba.protocol.sandbox.hbci.domain.HbciSandboxPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface HbciSandboxPaymentRepository extends JpaRepository<HbciSandboxPayment, Long> {

    Optional<HbciSandboxPayment> findByOwnerLoginAndOrderReference(String ownerLogin, String orderReference);

    List<HbciSandboxPayment> findByStatus(PaymentStatus paymentStatus);

    List<HbciSandboxPayment> findByOwnerLoginAndStatusInOrderByCreatedAtDesc(String ownerLogin, Set<PaymentStatus> statuses);
}
