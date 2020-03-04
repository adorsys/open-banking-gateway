package de.adorsys.opba.fintech.impl.database.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class RedirectUrlsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String redirectCode;
    private String redirectState;
    private String okURL;
    private String notOkURL;
}
