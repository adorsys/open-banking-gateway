package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2001;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import de.adorsys.opba.fintech.impl.tppclients.TppBankSearchClient;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankSearchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankSearchServiceTest {
    private static final String INVALID_KEYWORD = "sdhfsdlkjflksjdlkfvj";

    @InjectMocks
    private BankSearchService bankSearchService;

    @Mock
    private TppBankSearchClient tppBankSearchClient;
    @Mock
    private RestRequestContext restRequestContext;

    @Test
    void searchBank_nonExistingBank() {
        // Given
        int start = 1;
        int max = 10;
        BankSearchResponse bankSearchResponse = new BankSearchResponse().bankDescriptor(Collections.emptyList());
        when(restRequestContext.getRequestId()).thenReturn(UUID.randomUUID().toString());
        when(tppBankSearchClient.bankSearchGET(
                any(),
                any(),
                any(),
                any(),
                eq(INVALID_KEYWORD),
                eq(start),
                eq(max),
                any())).thenReturn(ResponseEntity.ok().body(bankSearchResponse));

        // When
        InlineResponse2001 actual = bankSearchService.searchBank(INVALID_KEYWORD, start, max);

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual.getBankDescriptor()).isNotNull();
        assertThat(actual.getBankDescriptor().size()).isEqualTo(0);
    }
}