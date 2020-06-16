package de.adorsys.opba.fintech.impl.database.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SinglePaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "single_payment_generator")
    @SequenceGenerator(name = "single_payment_generator", sequenceName = "single_payment_id_seq")
    private Long id;

    private String name;
    private String creditorIban;
    private String debitorIban;
    private String amount;
    private String currency;
    private String purpose;
    private OffsetDateTime initiationTime;
    private String transactionStatus;

    @OneToOne(fetch = FetchType.LAZY)
    private ConsentEntity consentEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountEntity accountEntity;
}
