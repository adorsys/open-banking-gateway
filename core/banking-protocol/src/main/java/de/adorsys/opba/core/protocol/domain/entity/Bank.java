package de.adorsys.opba.core.protocol.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Proxy(lazy = false)
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_id_generator")
    @SequenceGenerator(name = "bank_id_generator", sequenceName = "bank_id_sequence", allocationSize = 1)
    private Long id;

    String name;
    String bic;
    String bankCode;
}
