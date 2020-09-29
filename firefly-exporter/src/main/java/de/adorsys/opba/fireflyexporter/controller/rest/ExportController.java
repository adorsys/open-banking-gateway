package de.adorsys.opba.fireflyexporter.controller.rest;

import de.adorsys.opba.fireflyexporter.service.AccountExportService;
import de.adorsys.opba.fireflyexporter.service.TransactionExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ExportController {

    private static final String FIREFLY_TOKEN = "FIREFLY-TOKEN";

    private final AccountExportService accountExportService;
    private final TransactionExportService transactionExportService;

    @PostMapping("/{bankId}/export-accounts")
    public ResponseEntity<Object> exportAccounts(@RequestHeader(FIREFLY_TOKEN) String fireflyToken, @PathVariable String bankId) {
        accountExportService.exportAccounts(fireflyToken, bankId);
        return ResponseEntity.ok().build();
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
}
