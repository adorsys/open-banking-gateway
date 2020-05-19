package de.adorsys.opba.fintech.impl.database.entities;

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

@Getter
@Setter
@Entity
@Slf4j
@NoArgsConstructor
public class LoginEntity {
    public LoginEntity(UserEntity userEntity) {
        this.loginTime = OffsetDateTime.now();
        this.userEntity = userEntity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "login_generator")
    @SequenceGenerator(name = "login_generator", sequenceName = "login_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;

    @Column(nullable = false)
    private OffsetDateTime loginTime;


}
