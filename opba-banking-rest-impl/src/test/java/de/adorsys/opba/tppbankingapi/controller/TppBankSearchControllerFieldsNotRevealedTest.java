package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.BankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class TppBankSearchControllerFieldsNotRevealedTest extends BaseTppBankSearchControllerTest {

    private static final String TEST_BANK_NAME = "A test bank for fields not revealed";

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BankProfileJpaRepository bankProfileRepository;

    private Bank bank;
    private BankProfile profile;

    @BeforeEach
    void initData() {
        bank.setProfiles(Collections.singletonList(profile));
        bankRepository.save(bank);

        bank = bankRepository.findById(bank.getId()).orElseThrow();
        profile = bankProfileRepository.findById(profile.getId()).orElseThrow();
    }

    @Test
    void testBankSearchNoExtraFieldsRevealed() throws Exception {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        performBankSearchRequest(xRequestId, xTimestampUtc, TEST_BANK_NAME)
                .andExpect(jsonPath("$.bankDescriptor.length()").value("10"))
                .andExpect(jsonPath("$.bankDescriptor[0].bankName").value("Commerzbank"))
                .andExpect(jsonPath("$.bankDescriptor[0].bic").value("COBADEFFXXX"))
                .andExpect(jsonPath("$.bankDescriptor[0].bankCode").value("35640064"))
                .andExpect(jsonPath("$.bankDescriptor[0].uuid").value("291b2ca1-b35f-463e-ad94-2a1a26c09304"))
                .andReturn();
    }
}
