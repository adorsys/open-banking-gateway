package de.adorsys.opba.db.domain.entity.fintech;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FintechRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fintech_id_generator")
    @SequenceGenerator(name = "fintech_id_generator", sequenceName = "fintech-request_id_sequence")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Fintech fintech;

    @Column(nullable = false)
    private UUID xRequestId;
}
