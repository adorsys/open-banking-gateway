package de.adorsys.opba.protocol.sandbox.hbci.domain;

import de.adorsys.multibanking.domain.PaymentStatus;
import de.adorsys.opba.protocol.sandbox.hbci.config.dto.Transaction;
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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Currency;

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
    private String orderReference;

    @Column(nullable = false)
    private String deduceFrom;

    @Column(nullable = false)
    private String sendTo;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    private String remittanceUnstructured;

    // Nullable if payment is not yet authorized
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    private boolean instantPayment;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;

    @SuppressWarnings("checkstyle:MagicNumber") // It is magic number mapped to enum
    public int getHbciStatus() {
        switch (getStatus()) {
            case CANC:
                return 1;
            case RJCT:
                return 2;
            case PDNG:
                return 3;
            case ACCC:
                return 4;
            case ACSC:
                return 7;
            default:
                throw new IllegalStateException("Unmappable payment status: " + getStatus());
        }
    }

    public String getModifiedAtString() {
        return DateTimeFormatter.ISO_DATE_TIME.format(getModifiedAt().atOffset(ZoneOffset.UTC).toLocalDateTime());
    }

    public String getModifiedAtDateString() {
        return DateTimeFormatter.ISO_DATE.format(getModifiedAt().atOffset(ZoneOffset.UTC).toLocalDate());
    }

    public Transaction toTransaction(String accountNumber, BigDecimal balance) {
        Transaction transaction = new Transaction();
        if (getDeduceFrom().endsWith(accountNumber)) {
            transaction.setAmount(getAmount().negate().toString());
            transaction.setBalanceAfter(balance.subtract(getAmount()).toString());
        } else {
            transaction.setAmount(getAmount().toString());
            transaction.setBalanceAfter(balance.add(getAmount()).toString());
        }
        transaction.setBalanceBefore(balance.toString());
        transaction.setCurrency(Currency.getInstance(getCurrency()));
        transaction.setDate(getModifiedAtString());
        transaction.setPurpose(getRemittanceUnstructured());
        transaction.setTo(getSendTo());
        transaction.setFrom(getDeduceFrom());
        return transaction;
    }
}
