package de.adorsys.opba.fireflyexporter.controller.rest;

import de.adorsys.opba.fireflyexporter.repository.BankConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BankConsentController {

    private final BankConsentRepository bankConsentRepository;

    @Transactional
    @DeleteMapping("/consents/{bankId}")
    public ResponseEntity<Void> exportableAccounts(@PathVariable String bankId) {
        bankConsentRepository.deleteAllByBankId(bankId);
        return ResponseEntity.ok().build();
    }
}
