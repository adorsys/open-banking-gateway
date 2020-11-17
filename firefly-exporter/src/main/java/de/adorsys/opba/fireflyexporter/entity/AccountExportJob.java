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

    private static final int MAX_ERROR_LEN = 64;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_export_id_generator")
    @SequenceGenerator(name = "account_export_id_generator", sequenceName = "account_export_id_seq")
    private long id;

    private long accountsExported;

    private long numAccountsToExport;

    private long numAccountsErrored;

    private String lastErrorMessage;

    private boolean completed;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;

    public void setLastErrorMessage(String lastErrorMessage) {
        if (null == lastErrorMessage) {
            this.lastErrorMessage = null;
            return;
        }

        this.lastErrorMessage = lastErrorMessage.substring(0, Math.min(lastErrorMessage.length(), MAX_ERROR_LEN));
    }
}
