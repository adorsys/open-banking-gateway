package de.adorsys.opba.api.security.external.mapper;

import de.adorsys.opba.api.security.external.domain.HttpHeaders;
import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.domain.QueryParams;
import de.adorsys.opba.api.security.external.domain.signdata.AisListAccountsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.AisListTransactionsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankProfileDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankSearchDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.ConfirmConsentDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.PaymentInitiationDataToSign;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class FeignTemplateToDataToSignMapper {
    private static final String MISSING_HEADER_ERROR_MESSAGE = " header is missing";

    public AisListAccountsDataToSign mapToListAccounts(Map<String, Collection<String>> headers, Instant instant) {
        String operationType = extractRequiredValue(headers, HttpHeaders.X_OPERATION_TYPE);
        String xRequestId = extractRequiredValue(headers, HttpHeaders.X_REQUEST_ID);
        String bankId = extractNonRequiredValue(headers, HttpHeaders.BANK_ID);
        String fintechUserId = extractRequiredValue(headers, HttpHeaders.FINTECH_USER_ID);
        String redirectOkUrl = extractRequiredValue(headers, HttpHeaders.FINTECH_REDIRECT_URL_OK);
        String redirectNokUrl = extractRequiredValue(headers, HttpHeaders.FINTECH_REDIRECT_URL_NOK);

        return new AisListAccountsDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType), bankId, fintechUserId, redirectOkUrl, redirectNokUrl);
    }

    public AisListTransactionsDataToSign mapToListTransactions(Map<String, Collection<String>> headers, Map<String, String> queries, Instant instant) {
        String operationType = extractRequiredValue(headers, HttpHeaders.X_OPERATION_TYPE);
        String xRequestId = extractRequiredValue(headers, HttpHeaders.X_REQUEST_ID);
        String bankId = extractNonRequiredValue(headers, HttpHeaders.BANK_ID);
        String fintechUserId = extractRequiredValue(headers, HttpHeaders.FINTECH_USER_ID);
        String redirectOkUrl = extractRequiredValue(headers, HttpHeaders.FINTECH_REDIRECT_URL_OK);
        String redirectNokUrl = extractRequiredValue(headers, HttpHeaders.FINTECH_REDIRECT_URL_NOK);
        String dateFrom = queries.get(QueryParams.DATE_FROM);
        String dateTo = queries.get(QueryParams.DATE_TO);
        String entryReferenceFrom = queries.get(QueryParams.ENTRY_REFERENCE_FROM);
        String bookingStatus = queries.get(QueryParams.BOOKING_STATUS);
        String deltaList = queries.get(QueryParams.DELTA_LIST);

        return new AisListTransactionsDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType), bankId, fintechUserId,
                                                 redirectOkUrl, redirectNokUrl, dateFrom, dateTo, entryReferenceFrom, bookingStatus, deltaList
        );
    }

    public BankProfileDataToSign mapToBankProfile(Map<String, Collection<String>> headers, Instant instant) {
        String operationType = extractRequiredValue(headers, HttpHeaders.X_OPERATION_TYPE);
        String xRequestId = extractRequiredValue(headers, HttpHeaders.X_REQUEST_ID);

        return new BankProfileDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType));
    }

    public BankSearchDataToSign mapToBankSearch(Map<String, Collection<String>> headers, Map<String, String> queries, Instant instant) {
        String operationType = extractRequiredValue(headers, HttpHeaders.X_OPERATION_TYPE);
        String xRequestId = extractRequiredValue(headers, HttpHeaders.X_REQUEST_ID);
        String keyword = queries.get(QueryParams.KEYWORD);

        return new BankSearchDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType), keyword);
    }

    public ConfirmConsentDataToSign mapToConfirmConsent(Map<String, Collection<String>> headers, Instant instant) {
        String operationType = extractRequiredValue(headers, HttpHeaders.X_OPERATION_TYPE);
        String xRequestId = extractRequiredValue(headers, HttpHeaders.X_REQUEST_ID);

        return new ConfirmConsentDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType));
    }

    public PaymentInitiationDataToSign mapToPaymentInitiation(Map<String, Collection<String>> headers, Instant instant) {
        String operationType = extractRequiredValue(headers, HttpHeaders.X_OPERATION_TYPE);
        String xRequestId = extractRequiredValue(headers, HttpHeaders.X_REQUEST_ID);
        String bankId = extractNonRequiredValue(headers, HttpHeaders.BANK_ID);
        String fintechUserId = extractRequiredValue(headers, HttpHeaders.FINTECH_USER_ID);
        String redirectOkUrl = extractRequiredValue(headers, HttpHeaders.FINTECH_REDIRECT_URL_OK);
        String redirectNokUrl = extractRequiredValue(headers, HttpHeaders.FINTECH_REDIRECT_URL_NOK);

        return new PaymentInitiationDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType), bankId, fintechUserId, redirectOkUrl, redirectNokUrl);
    }

    private String extractRequiredValue(Map<String, Collection<String>> values, String valueName) {
        if (values.get(valueName) == null) {
            throw new IllegalStateException(valueName + MISSING_HEADER_ERROR_MESSAGE);
        }

        return values.get(valueName).stream().findFirst()
                       .orElseThrow(() -> new IllegalStateException(valueName + MISSING_HEADER_ERROR_MESSAGE));
    }

    private String extractNonRequiredValue(Map<String, Collection<String>> values, String valueName) {
        if (values.get(valueName) == null) {
            return null;
        }

        return values.get(valueName).stream().findFirst().orElse(null);
    }
}
