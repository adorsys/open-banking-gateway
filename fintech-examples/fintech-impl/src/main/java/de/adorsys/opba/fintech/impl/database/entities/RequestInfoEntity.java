package de.adorsys.opba.fintech.impl.database.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.LocalDate;

@Data
@Entity
public class RequestInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_info_generator")
    @SequenceGenerator(name = "request_info_generator", sequenceName = "request_info_id_seq")
    private Long id;

    @Column(nullable = false)
    private String xsrfToken;

    @Column(nullable = false)
    private String bankId;

    @Column(nullable = false)
    private RequestAction requestAction;

    private String accountId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String entryReferenceFrom;
    private String bookingStatus;
    private Boolean deltaList;
}
