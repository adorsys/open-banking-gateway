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
import javax.persistence.SequenceGenerator;

@Getter
@Setter
@Entity
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_generator")
    @SequenceGenerator(name = "account_generator", sequenceName = "account_id_seq")
    private Long id;

    private String name;
    private String resourceId;
    private String bankId;
    private String iban;
    private String status;
    private String currency;

    // this attribute becomes true, when tpp does no more know this account
    private Boolean becameUnknown;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;
}
