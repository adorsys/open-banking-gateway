package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.tppbankingapi.bankinfo.model.generated.BankInfoResponse;
import de.adorsys.opba.tppbankingapi.bankinfo.model.generated.SearchBankinfoBody;
import de.adorsys.opba.tppbankingapi.service.BankInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestTppBankInfoController {

    @Mock
    private BankInfoService bankInfoService;

    @InjectMocks
    private TppBankInfoController controller;

    private UUID xRequestID;
    private SearchBankinfoBody searchBody;
    private String fintechID;
    private String xRequestSignature;
    private BankInfoResponse mockResponse;

    @BeforeEach
    void setUp() {
        xRequestID = UUID.randomUUID();
        searchBody = new SearchBankinfoBody();
        searchBody.setIban("DE89370400440532013000");
        fintechID = "testFintechId";
        xRequestSignature = "testSignature";

        mockResponse = new BankInfoResponse();
        mockResponse.setBankName("Test Bank");
        mockResponse.setBankCode("37040044");
        mockResponse.setBic("COBADEFFXXX");
        mockResponse.setUuid(UUID.randomUUID());
    }

    @Test
    void getBankInfoByIban_shouldReturnBankInfo_whenValidIbanProvided() {
        // Given
        when(bankInfoService.getBankInfoByIban(searchBody.getIban())).thenReturn(mockResponse);

        // When
        ResponseEntity<BankInfoResponse> result = controller.getBankInfoByIban(
                xRequestID, searchBody, fintechID, xRequestSignature
        );

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(mockResponse.getBankName(), result.getBody().getBankName());
        assertEquals(mockResponse.getBankCode(), result.getBody().getBankCode());
        assertEquals(mockResponse.getBic(), result.getBody().getBic());
        assertEquals(mockResponse.getUuid(), result.getBody().getUuid());

        verify(bankInfoService, times(1)).getBankInfoByIban(searchBody.getIban());
    }

    @Test
    void getBankInfoByIban_shouldReturnNotFound_whenBankInfoServiceReturnsNull() {
        // Given
        when(bankInfoService.getBankInfoByIban(anyString())).thenReturn(null);

        // When
        ResponseEntity<BankInfoResponse> result = controller.getBankInfoByIban(
                xRequestID, searchBody, fintechID, xRequestSignature
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());

        verify(bankInfoService, times(1)).getBankInfoByIban(searchBody.getIban());
    }

    @Test
    void getBankInfoByIban_shouldReturnNotFound_whenInvalidIbanProvided() {
        // Given
        searchBody.setIban("DE89370400440532013001"); // Different IBAN
        when(bankInfoService.getBankInfoByIban(searchBody.getIban())).thenReturn(null);

        // When
        ResponseEntity<BankInfoResponse> result = controller.getBankInfoByIban(
                xRequestID, searchBody, fintechID, xRequestSignature
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());

        verify(bankInfoService, times(1)).getBankInfoByIban(searchBody.getIban());
    }

    @Test
    void getBankInfoByIban_shouldCallServiceWithCorrectIban() {
        // Given
        String testIban = "DE89370400440532013000";
        searchBody.setIban(testIban);
        when(bankInfoService.getBankInfoByIban(testIban)).thenReturn(mockResponse);

        // When
        controller.getBankInfoByIban(xRequestID, searchBody, fintechID, xRequestSignature);

        // Then
        verify(bankInfoService, times(1)).getBankInfoByIban(testIban);
    }

    @Test
    void getBankInfoByIban_shouldReturnNotFound_whenIbanIsMissing() {
        searchBody.setIban(null);
        when(bankInfoService.getBankInfoByIban(null)).thenReturn(null);

        ResponseEntity<BankInfoResponse> result = controller.getBankInfoByIban(
                xRequestID, searchBody, fintechID, xRequestSignature
        );

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());

        verify(bankInfoService, times(1)).getBankInfoByIban(null);
    }

}