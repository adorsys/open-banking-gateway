package de.adorsys.opba.fireflyexporter.controller.rest;

import de.adorsys.opba.fireflyexporter.repository.BankConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BankConsentController {

    private final BankConsentRepository bankConsentRepository;

    @Transactional
    @DeleteMapping("/consents/{bankProfileId}")
    public ResponseEntity<Void> exportableAccounts(@PathVariable UUID bankProfileId) {
        bankConsentRepository.deleteByBankProfileUuid(bankProfileId);
        return ResponseEntity.ok().build();
    }
}
