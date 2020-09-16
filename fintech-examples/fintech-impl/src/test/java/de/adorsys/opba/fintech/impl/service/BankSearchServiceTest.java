package de.adorsys.opba.fintech.impl.service;

import de.adorsys.opba.fintech.api.model.generated.InlineResponse2001;
import de.adorsys.opba.fintech.impl.config.FintechUiConfig;
import de.adorsys.opba.fintech.impl.controller.utils.RestRequestContext;
import de.adorsys.opba.fintech.impl.database.entities.RedirectUrlsEntity;
import de.adorsys.opba.fintech.impl.tppclients.TppBankSearchClient;
import de.adorsys.opba.tpp.banksearch.api.model.generated.BankSearchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void searchBank() {
        // Given
        when(restRequestContext.getRequestId()).thenReturn(UUID.randomUUID().toString());

        // When
        InlineResponse2001 actual = bankSearchService.searchBank("INVALID_KEYWORD", 1, 10);

        // Then
        assertThat(actual).isNotNull();
        assertThat(actual.getBankDescriptor()).isNotNull();
        assertThat(actual.getBankDescriptor().size()).isEqualTo(0);
    }
}