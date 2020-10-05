package de.adorsys.opba.fireflyexporter.service;

import de.adorsys.opba.fireflyexporter.entity.TransactionExportJob;
import de.adorsys.opba.fireflyexporter.repository.TransactionExportJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionExportService {

    private final TransactionExportJobRepository transactionExportJobRepository;
    private final FireFlyTransactionExporter exporter;

    @Transactional
    public ResponseEntity<Long> exportTransactions(String fireflyToken, String bankId, List<String> accountIds, LocalDate dateFrom, LocalDate dateTo) {
        TransactionExportJob exportJob = new TransactionExportJob();
        exportJob.setNumAccountsToExport(accountIds.size());
        exportJob = transactionExportJobRepository.save(exportJob);
        exporter.exportToFirefly(fireflyToken, exportJob.getId(), bankId, accountIds, dateFrom, dateTo);
        return ResponseEntity.ok(exportJob.getId());
    }
}
