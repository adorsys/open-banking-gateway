package de.adorsys.opba.fintech.impl.database.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_generator")
    @SequenceGenerator(name = "payment_generator", sequenceName = "payment_id_seq")
    private Long id;

    private String bankId;
    private String accountId;
    private String tppAuthId;
    private UUID tppServiceSessionId;
    private String paymentProduct;

    @Builder.Default
    @Column(nullable = false)
    private OffsetDateTime creationTime = OffsetDateTime.now();

    private boolean paymentConfirmed;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;
}
