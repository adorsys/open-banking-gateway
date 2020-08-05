package de.adorsys.opba.protocol.sandbox.hbci.domain;

import de.adorsys.multibanking.domain.PaymentStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class HbciSandboxPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hbci_sandbox_payment_id_generator")
    @SequenceGenerator(name = "hbci_sandbox_payment_id_generator", sequenceName = "hbci_sandbox_payment_id_seq")
    private long id;

    @Column(nullable = false)
    private String ownerLogin;

    @Column(nullable = false)
    private String from;

    @Column(nullable = false)
    private String to;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;
}
