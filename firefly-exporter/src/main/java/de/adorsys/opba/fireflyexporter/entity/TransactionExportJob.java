package de.adorsys.opba.fireflyexporter.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.time.Instant;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class TransactionExportJob {

    private static final int MAX_ERROR_LEN = 64;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_export_id_generator")
    @SequenceGenerator(name = "transaction_export_id_generator", sequenceName = "transaction_export_id_seq")
    private long id;

    private long accountsExported;

    private long numAccountsToExport;

    private long numAccountsErrored;

    private long numTransactionsExported;

    private long numTransactionsErrored;

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
