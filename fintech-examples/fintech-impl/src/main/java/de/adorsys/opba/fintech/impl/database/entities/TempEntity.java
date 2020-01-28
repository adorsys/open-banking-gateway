package de.adorsys.opba.fintech.impl.database.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.OffsetDateTime;


@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TempEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long primaryKey;
    private String password;
    private OffsetDateTime lastLogin;
    private String xsrfToken;
}
