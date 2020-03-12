package de.adorsys.opba.fintech.impl.database.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@Entity
public class RequestInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String xsrfToken;
    private String bankId;
    private RequestAction requestAction;
    private  String accountId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String entryReferenceFrom;
    private String bookingStatus;
    private Boolean deltaList;
}
