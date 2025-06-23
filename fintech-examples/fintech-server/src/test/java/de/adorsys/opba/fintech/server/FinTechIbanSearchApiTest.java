package de.adorsys.opba.fintech.server;

import de.adorsys.opba.fintech.api.model.generated.InlineResponseBankInfo;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import de.adorsys.opba.fintech.impl.mapper.BankInfoMapper;
import de.adorsys.opba.fintech.impl.service.IbanSearchService;
import de.adorsys.opba.fintech.impl.service.exceptions.InvalidIbanException;
import de.adorsys.opba.fintech.impl.tppclients.TppIbanSearchClient;
import de.adorsys.opba.tpp.bankinfo.api.model.generated.BankInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FinTech IBAN Search API Test Suite")
class FinTechIbanSearchApiTest {

    private static final String VALID_IBAN = "DE89370400440532013000";
    private static final String INVALID_IBAN = "INVALID_IBAN";
    private static final String REQUEST_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final UUID UUID_REQUEST_ID = UUID.randomUUID();
    private static final String XSRF_TOKEN = "test-token";

    @Nested
    @DisplayName("IbanSearchService Tests")
    class IbanSearchServiceTests {

        @Mock
        private TppIbanSearchClient tppIbanSearchClient;

        @Mock
        private RestRequestContext restRequestContext;

        @Mock
        private BankInfoMapper bankInfoMapper;

        @InjectMocks
        private IbanSearchService ibanSearchService;

        @BeforeEach
        void setUp() {
            lenient().when(restRequestContext.getRequestId()).thenReturn(REQUEST_ID);
        }

        @Test
        void shouldReturnMappedBankInfoForValidIban() {
            BankInfoResponse tppResponse = new BankInfoResponse();
            InlineResponseBankInfo expected = new InlineResponseBankInfo();

            when(tppIbanSearchClient.getBankInfoByIban(any(), any(), any(), any())).thenReturn(ResponseEntity.ok(tppResponse));
            when(bankInfoMapper.mapFromTppToFintech(tppResponse)).thenReturn(expected);

            var result = ibanSearchService.searchByIban(VALID_IBAN);

            assertEquals(expected, result);
            verify(tppIbanSearchClient).getBankInfoByIban(eq(UUID.fromString(REQUEST_ID)), any(), any(), any());
            verify(bankInfoMapper).mapFromTppToFintech(tppResponse);
        }

        @Test
        void shouldThrowForNullOrEmptyIban() {
            assertThrows(InvalidIbanException.class, () -> ibanSearchService.searchByIban(null));
            assertThrows(InvalidIbanException.class, () -> ibanSearchService.searchByIban("   "));
            verify(tppIbanSearchClient, never()).getBankInfoByIban(any(), any(), any(), any());
        }

        @Test
        void shouldThrowWhenTppReturnsNull() {
            when(tppIbanSearchClient.getBankInfoByIban(any(), any(), any(), any())).thenReturn(ResponseEntity.ok(null));
            var ex = assertThrows(InvalidIbanException.class, () -> ibanSearchService.searchByIban(VALID_IBAN));
            assertEquals("An unexpected error occurred while processing your request. Please try again later.", ex.getMessage());
        }

        @Test
        void shouldThrowOnTppException() {
            when(tppIbanSearchClient.getBankInfoByIban(any(), any(), any(), any())).thenThrow(new RuntimeException("error"));
            var ex = assertThrows(InvalidIbanException.class, () -> ibanSearchService.searchByIban(VALID_IBAN));
            assertTrue(ex.getMessage().contains("unexpected error"));
        }

        @Test
        void shouldSetCorrectIbanInRequestBody() {
            BankInfoResponse tppResponse = new BankInfoResponse();
            when(tppIbanSearchClient.getBankInfoByIban(any(), any(), any(), any())).thenReturn(ResponseEntity.ok(tppResponse));
            ibanSearchService.searchByIban(VALID_IBAN);
            verify(tppIbanSearchClient).getBankInfoByIban(eq(UUID.fromString(REQUEST_ID)), argThat(b -> VALID_IBAN.equals(b.getIban())), any(), any());
        }
    }
}
