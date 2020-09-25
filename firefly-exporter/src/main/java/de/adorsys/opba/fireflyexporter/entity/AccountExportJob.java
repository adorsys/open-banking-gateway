package de.adorsys.opba.fireflyexporter.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.Instant;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AccountExportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_export_id_generator")
    @SequenceGenerator(name = "account_export_id_generator", sequenceName = "account_export_id_seq")
    private long id;


    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;
}
