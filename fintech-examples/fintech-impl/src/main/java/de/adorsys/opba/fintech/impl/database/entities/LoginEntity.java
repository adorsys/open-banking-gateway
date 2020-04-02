package de.adorsys.opba.fintech.impl.database.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Id;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class LoginEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "login_generator")
    @SequenceGenerator(name = "login_generator", sequenceName = "login_id_seq")
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime loginTime;
}
