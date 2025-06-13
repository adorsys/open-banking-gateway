package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.domain.entity.BankProfile;
import de.adorsys.opba.db.repository.jpa.BankProfileJpaRepository;
import de.adorsys.opba.db.repository.jpa.BankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class TppBankSearchControllerFieldsNotRevealedTest extends BaseTppBankSearchControllerTest {

    private static final String TEST_BANK_NAME = "A-test-bank-for-fields-not-revealed";

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BankProfileJpaRepository bankProfileRepository;

    private Bank bank;
    private BankProfile profile;

    @BeforeEach
    public void initData() {
        bank = new Bank();
        bank.setUuid(UUID.randomUUID());
        bank.setName(TEST_BANK_NAME);
        bank.setActive(true);
        profile = new BankProfile();
        profile.setActive(true);
        profile.setProtocolConfiguration("Configuration");
        profile.setUuid(UUID.randomUUID());

        bank = bankRepository.save(bank);
        profile.setBank(bank);
        bankProfileRepository.save(profile);

        bank = bankRepository.findById(bank.getId()).orElseThrow();
        profile = bankProfileRepository.findById(profile.getId()).orElseThrow();
    }

    @Test
    void testBankSearchNoExtraFieldsRevealed() throws Exception {
        UUID xRequestId = UUID.randomUUID();
        Instant xTimestampUtc = Instant.now();

        performBankSearchRequest(xRequestId, xTimestampUtc, TEST_BANK_NAME)
                .andExpect(jsonPath("$.bankDescriptor.length()").value("1"))
                .andExpect(jsonPath("$.bankDescriptor[0].bankName").value(TEST_BANK_NAME))
                .andExpect(jsonPath("$.bankDescriptor[0].profiles[0].protocolConfiguration").doesNotHaveJsonPath())
                .andReturn();
    }
}
