package de.adorsys.opba.fintech.impl.database.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class RedirectUrlsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "redirect_urls_generator")
    @SequenceGenerator(name = "redirect_urls_generator", sequenceName = "redirect_urls_id_seq", allocationSize = 1)
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
