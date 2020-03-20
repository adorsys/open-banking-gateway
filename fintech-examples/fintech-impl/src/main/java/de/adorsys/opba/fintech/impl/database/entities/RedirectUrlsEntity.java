package de.adorsys.opba.fintech.impl.database.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Id;

@Data
@Entity
public class RedirectUrlsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "redirect_urls_generator")
    @SequenceGenerator(name = "redirect_urls_generator", sequenceName = "redirect_urls_id_seq")
    private Long id;

    @Column(nullable = false)
    private String redirectState;

    @Column(nullable = false)
    private String okURL;

    @Column(nullable = false)
    private String notOkURL;

    @Column(nullable = false)
    private String redirectCode;
}
