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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.adorsys.opba.fireflyexporter.controller.rest.Consts.FIREFLY_TOKEN;

@RestController
@RequiredArgsConstructor
public class ExportController {

    private final AccountExportJobRepository accountExportJobRepository;
    private final TransactionExportJobRepository transactionExportJobRepository;
    private final AccountExportService accountExportService;
    private final TransactionExportService transactionExportService;

    @PostMapping("/{bankProfileId}/export-accounts")
    public ResponseEntity<Long> exportAccounts(@RequestHeader(FIREFLY_TOKEN) String fireflyToken, @PathVariable UUID bankProfileId) {
        return accountExportService.exportAccounts(fireflyToken, bankProfileId);
    }

    @PostMapping("/{bankProfileId}/{accountIds}/export-transactions")
    public ResponseEntity<Long> exportTransactions(
            @RequestHeader(FIREFLY_TOKEN) String fireflyToken,
            @PathVariable UUID bankProfileId,
            @PathVariable List<String> accountIds,
            @RequestParam(value = "dateFrom", defaultValue = "") LocalDate dateFrom,
            @RequestParam(value = "dateTo", defaultValue = "") LocalDate dateTo
    ) {
        return transactionExportService.exportTransactions(fireflyToken, bankProfileId, accountIds, dateFrom, dateTo);
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
