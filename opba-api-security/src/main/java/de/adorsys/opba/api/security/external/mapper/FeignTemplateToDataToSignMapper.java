package de.adorsys.opba.api.security.external.mapper;

import de.adorsys.opba.api.security.external.domain.HttpHeaders;
import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.domain.QueryParams;
import de.adorsys.opba.api.security.external.domain.signdata.AisListAccountsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.AisListTransactionsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankProfileDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankSearchDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.ConfirmConsentDataToSign;
import feign.RequestTemplate;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class FeignTemplateToDataToSignMapper {
    private static final String MISSING_HEADER_ERROR_MESSAGE = " header is missing";

    public AisListAccountsDataToSign mapToListAccounts(RequestTemplate requestTemplate, Instant instant) {
        Map<String, Collection<String>> headers = requestTemplate.headers();

        String operationType = extractRequiredValue(headers, HttpHeaders.X_OPERATION_TYPE);
        String xRequestId = extractRequiredValue(headers, HttpHeaders.X_REQUEST_ID);
        String bankId = extractNonRequiredValue(headers, HttpHeaders.BANK_ID);
        String fintechUserId = extractRequiredValue(headers, HttpHeaders.FINTECH_USER_ID);
        String redirectOkUrl = extractRequiredValue(headers, HttpHeaders.FINTECH_REDIRECT_URL_OK);
        String redirectNokUrl = extractRequiredValue(headers, HttpHeaders.FINTECH_REDIRECT_URL_NOK);

        return new AisListAccountsDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType), bankId, fintechUserId, redirectOkUrl, redirectNokUrl);
    }

    public AisListTransactionsDataToSign mapToListTransactions(RequestTemplate requestTemplate, Instant instant) {
        Map<String, Collection<String>> headers = requestTemplate.headers();
        Map<String, Collection<String>> queries = requestTemplate.queries();

        String operationType = extractRequiredValue(headers, HttpHeaders.X_OPERATION_TYPE);
        String xRequestId = extractRequiredValue(headers, HttpHeaders.X_REQUEST_ID);
        String bankId = extractNonRequiredValue(headers, HttpHeaders.BANK_ID);
        String fintechUserId = extractRequiredValue(headers, HttpHeaders.FINTECH_USER_ID);
        String redirectOkUrl = extractRequiredValue(headers, HttpHeaders.FINTECH_REDIRECT_URL_OK);
        String redirectNokUrl = extractRequiredValue(headers, HttpHeaders.FINTECH_REDIRECT_URL_NOK);
        String dateFrom = extractNonRequiredValue(queries, QueryParams.DATE_FROM);
        String dateTo = extractNonRequiredValue(queries, QueryParams.DATE_TO);
        String entryReferenceFrom = extractNonRequiredValue(queries, QueryParams.ENTRY_REFERENCE_FROM);
        String bookingStatus = extractNonRequiredValue(queries, QueryParams.BOOKING_STATUS);
        String deltaList = extractNonRequiredValue(queries, QueryParams.DELTA_LIST);

        return new AisListTransactionsDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType), bankId, fintechUserId,
                redirectOkUrl, redirectNokUrl, dateFrom, dateTo, entryReferenceFrom, bookingStatus, deltaList
        );
    }

    public BankProfileDataToSign mapToBankProfile(RequestTemplate requestTemplate, Instant instant) {
        Map<String, Collection<String>> headers = requestTemplate.headers();

        String operationType = extractRequiredValue(headers, HttpHeaders.X_OPERATION_TYPE);
        String xRequestId = extractRequiredValue(headers, HttpHeaders.X_REQUEST_ID);

        return new BankProfileDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType));
    }

    public BankSearchDataToSign mapToBankSearch(RequestTemplate requestTemplate, Instant instant) {
        Map<String, Collection<String>> headers = requestTemplate.headers();
        Map<String, Collection<String>> queries = requestTemplate.queries();

        String operationType = extractRequiredValue(headers, HttpHeaders.X_OPERATION_TYPE);
        String xRequestId = extractRequiredValue(headers, HttpHeaders.X_REQUEST_ID);
        String keyword = extractRequiredValue(queries, QueryParams.KEYWORD);

        return new BankSearchDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType), keyword);
    }

    public ConfirmConsentDataToSign mapToConfirmConsent(RequestTemplate requestTemplate, Instant instant) {
        Map<String, Collection<String>> headers = requestTemplate.headers();

        String operationType = extractRequiredValue(headers, HttpHeaders.X_OPERATION_TYPE);
        String xRequestId = extractRequiredValue(headers, HttpHeaders.X_REQUEST_ID);

        return new ConfirmConsentDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType));
    }

    private String extractRequiredValue(Map<String, Collection<String>> headers, String valueName) {
        return headers.get(valueName).stream().findFirst()
                       .orElseThrow(() -> new IllegalStateException(valueName + MISSING_HEADER_ERROR_MESSAGE));
    }

    private String extractNonRequiredValue(Map<String, Collection<String>> headers, String valueName) {
        return headers.get(valueName).stream().findFirst().orElse(null);
    }
}
