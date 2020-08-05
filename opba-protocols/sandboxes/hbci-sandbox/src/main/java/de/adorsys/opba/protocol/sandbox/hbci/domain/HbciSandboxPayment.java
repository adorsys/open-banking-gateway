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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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

    // Nullable if payment is not yet authorized
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;
    
    public int getHbciStatus() {
        //        1: in Terminierung
//        2: abgelehnt von erster Inkassostelle
//        3: in Bearbeitung
//        4: Creditoren-seitig verarbeitet, Buchung veranlasst
//        5: R-Transaktion wurde veranlasst
//        6: Auftrag fehlgeschagen
//        7: Auftrag ausgeführt; Geld für den Zahlungsempfänger verfügbar
//        8: Abgelehnt durch Zahlungsdienstleister des Zahlers
//        9: Abgelehnt durch Zahlungsdienstleister des Zahlungsempfängers
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
        return DateTimeFormatter.ISO_DATE_TIME.format(getModifiedAt().atOffset(ZoneOffset.UTC));
    }

    public String getModifiedAtDateString() {
        return DateTimeFormatter.ISO_DATE.format(getModifiedAt().atOffset(ZoneOffset.UTC));
    }
}
