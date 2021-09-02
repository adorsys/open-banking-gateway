package de.adorsys.opba.tppbankingapi.service;

import de.adorsys.opba.tppbankingapi.search.model.generated.BankDescriptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static de.adorsys.opba.tppbankingapi.TestProfiles.ONE_TIME_POSTGRES_ON_DISK;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles(ONE_TIME_POSTGRES_ON_DISK)
@AutoConfigureMockMvc
class BankServiceTest {
    private static String NON_EXISTING_KEYWORD = "sdfsd";

    @Autowired
    private BankService bankService;

    @Test
    void getBankProfile() {
        // Given
        int start = 1;
        int end = 10;

        // When
        List<BankDescriptor> banks = bankService.getBanks(NON_EXISTING_KEYWORD, start, end, true);

        // Then
        assertThat(banks).isNotNull();
        assertThat(banks.size()).isEqualTo(0);
    }
}