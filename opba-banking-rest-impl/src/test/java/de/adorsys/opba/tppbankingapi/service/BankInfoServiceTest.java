package de.adorsys.opba.tppbankingapi.service;

import de.adorsys.opba.db.domain.entity.Bank;
import de.adorsys.opba.db.repository.BankInfoRepositoryImpl;
import de.adorsys.opba.tppbankingapi.bankinfo.model.generated.BankInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import org.iban4j.Iban;
import org.iban4j.InvalidCheckDigitException;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankInfoServiceTest {

    @Mock
    private BankInfoRepositoryImpl bankInfoRepository;

    @InjectMocks
    private BankInfoService bankInfoService;

    private Bank mockBank;
    private String validIban;
    private String expectedBankCode;

    @BeforeEach
    void setUp() {
        validIban = "DE89370400440532013000";
        expectedBankCode = "37040044";

        mockBank = new Bank();
        mockBank.setName("Commerzbank AG");
        mockBank.setBankCode(expectedBankCode);
        mockBank.setBic("COBADEFFXXX");
        mockBank.setUuid(UUID.randomUUID());
    }

    @Test
    void getBankInfoByIban_shouldReturnBankInfo_whenValidIbanAndBankExists() {
        // Given
        when(bankInfoRepository.findByIban(expectedBankCode, false))
                .thenReturn(Optional.of(mockBank));

        // When
        BankInfoResponse result = bankInfoService.getBankInfoByIban(validIban);

        // Then
        assertNotNull(result);
        assertEquals(mockBank.getName(), result.getBankName());
        assertEquals(mockBank.getBankCode(), result.getBankCode());
        assertEquals(mockBank.getBic(), result.getBic());
        assertEquals(mockBank.getUuid(), result.getUuid());

        verify(bankInfoRepository, times(1)).findByIban(expectedBankCode, false);
    }

    @Test
    void ibanValueOf_shouldThrowInvalidCheckDigitException_forBadChecksum() {
        String invalidIban = "DE00370400440532013000"; // Wrong check digits

        assertThrows(InvalidCheckDigitException.class, () -> {
            Iban.valueOf(invalidIban);
        });
    }

    @Test
    void getBankInfoByIban_shouldReturnNull_whenBankNotFound() {
        when(bankInfoRepository.findByIban(expectedBankCode, false))
                .thenReturn(Optional.empty());

        BankInfoResponse result = bankInfoService.getBankInfoByIban(validIban);

        assertNull(result);
        verify(bankInfoRepository, times(1)).findByIban(expectedBankCode, false);
    }

    @Test
    void getBankInfoByIban_shouldReturnNull_whenInvalidIbanFormat() {
        String invalidIban = "DEINVALID_IBAN";

        assertDoesNotThrow(() -> {
            BankInfoResponse result = bankInfoService.getBankInfoByIban(invalidIban);
            assertNull(result);
        });

        verify(bankInfoRepository, never()).findByIban(anyString(), anyBoolean());
    }

    @Test
    void getBankInfoByIban_shouldReturnNull_whenIbanIsNull() {
        BankInfoResponse result = bankInfoService.getBankInfoByIban(null);

        assertNull(result);
        verify(bankInfoRepository, never()).findByIban(anyString(), anyBoolean());
    }

    @Test
    void getBankInfoByIban_shouldReturnNull_whenIbanIsEmpty() {
        BankInfoResponse result = bankInfoService.getBankInfoByIban("");

        assertNull(result);
        verify(bankInfoRepository, never()).findByIban(anyString(), anyBoolean());
    }


    @Test
    void getBankInfoByIban_shouldExtractCorrectBankCode_fromDifferentIbans() {
        String[] testCases = {
                "DE89370400440532013000", // Expected bank code: 37040044
                "DE44500105175407324931", // Expected bank code: 50010517
                "DE91100000000123456789"  // Expected bank code: 10000000
        };

        String[] expectedBankCodes = {
                "37040044",
                "50010517",
                "10000000"
        };

        for (int i = 0; i < testCases.length; i++) {
            // Given
            String testIban = testCases[i];
            String expectedCode = expectedBankCodes[i];
            Bank testBank = new Bank();
            testBank.setName("Test Bank " + i);
            testBank.setBankCode(expectedCode);
            testBank.setBic("TESTBIC" + i);
            testBank.setUuid(UUID.randomUUID());

            when(bankInfoRepository.findByIban(expectedCode, false))
                    .thenReturn(Optional.of(testBank));

            BankInfoResponse result = bankInfoService.getBankInfoByIban(testIban);

            assertNotNull(result, "Result should not be null for IBAN: " + testIban);
            assertEquals(expectedCode, result.getBankCode(),
                    "Bank code should match for IBAN: " + testIban);

            verify(bankInfoRepository, times(1)).findByIban(expectedCode, false);
            reset(bankInfoRepository);
        }
    }

    @Test
    void getBankInfoByIban_shouldHandleRepositoryException() {
        when(bankInfoRepository.findByIban(anyString(), anyBoolean()))
                .thenThrow(new RuntimeException("Database connection error"));

        assertThrows(RuntimeException.class, () ->
                bankInfoService.getBankInfoByIban(validIban)
        );
    }

    @Test
    void getBankInfoByIban_shouldCallRepositoryWithCorrectParameters() {
        when(bankInfoRepository.findByIban(expectedBankCode, false))
                .thenReturn(Optional.of(mockBank));

        bankInfoService.getBankInfoByIban(validIban);

        verify(bankInfoRepository, times(1)).findByIban(expectedBankCode, false);
    }
}