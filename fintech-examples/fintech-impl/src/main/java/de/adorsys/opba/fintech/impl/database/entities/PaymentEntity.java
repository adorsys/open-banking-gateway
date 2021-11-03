package de.adorsys.opba.fintech.impl.database.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
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
