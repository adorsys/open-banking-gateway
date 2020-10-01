package de.adorsys.opba.fireflyexporter.controller.rest;

import de.adorsys.opba.fireflyexporter.entity.AccountExportJob;
import de.adorsys.opba.fireflyexporter.entity.TransactionExportJob;
import de.adorsys.opba.fireflyexporter.repository.AccountExportJobRepository;
import de.adorsys.opba.fireflyexporter.repository.TransactionExportJobRepository;
import de.adorsys.opba.fireflyexporter.service.AccountExportService;
import de.adorsys.opba.fireflyexporter.service.TransactionExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ExportController {

    private static final String FIREFLY_TOKEN = "FIREFLY-TOKEN";

    private final AccountExportJobRepository accountExportJobRepository;
    private final TransactionExportJobRepository transactionExportJobRepository;
    private final AccountExportService accountExportService;
    private final TransactionExportService transactionExportService;

    @PostMapping("/{bankId}/export-accounts")
    public ResponseEntity<Long> exportAccounts(@RequestHeader(FIREFLY_TOKEN) String fireflyToken, @PathVariable String bankId) {
        return accountExportService.exportAccounts(fireflyToken, bankId);
    }

    @PostMapping("/{bankId}/export-transactions")
    public ResponseEntity<Object> exportTransactions(
            @RequestHeader(FIREFLY_TOKEN) String fireflyToken,
            @PathVariable String bankId,
            @RequestParam(value = "dateFrom", defaultValue = "") LocalDate dateFrom,
            @RequestParam(value = "dateTo", defaultValue = "") LocalDate dateTo
    ) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/export-accounts/{jobId}")
    public Optional<AccountExportJob> accountJobStatus(@PathVariable long jobId) {
        return accountExportJobRepository.findById(jobId);
    }

    @GetMapping("/export-transactions/{jobId}")
    public Optional<TransactionExportJob> transactionJobStatus(@PathVariable long jobId) {
        return transactionExportJobRepository.findById(jobId);
    }
}
