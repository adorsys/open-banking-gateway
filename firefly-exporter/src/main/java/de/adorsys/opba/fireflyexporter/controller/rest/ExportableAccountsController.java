package de.adorsys.opba.fireflyexporter.controller.rest;

import de.adorsys.opba.fireflyexporter.dto.ExportableAccount;
import de.adorsys.opba.fireflyexporter.service.ExportableAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.adorsys.opba.fireflyexporter.controller.rest.Consts.FIREFLY_TOKEN;

@RestController
@RequiredArgsConstructor
public class ExportableAccountsController {

    private final ExportableAccountService exportableAccountService;

    @GetMapping("/{bankId}/exportable-accounts")
    public ResponseEntity<List<ExportableAccount>> exportableAccounts(@RequestHeader(FIREFLY_TOKEN) String fireflyToken, @PathVariable String bankId) {
        return exportableAccountService.exportableAccounts(fireflyToken, bankId);
    }
}
