package de.adorsys.opba.api.security.external.mapper;

import de.adorsys.opba.api.security.external.domain.HttpHeaders;
import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.domain.QueryParams;
import de.adorsys.opba.api.security.external.domain.signdata.AisListAccountsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.AisListTransactionsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankProfileDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankSearchDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.ConfirmConsentDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.PaymentInfoDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.PaymentInitiationDataToSign;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.UUID;

public class HttpRequestToDataToSignMapper {

    public AisListAccountsDataToSign mapToListAccounts(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return new AisListAccountsDataToSign(
                UUID.fromString(xRequestId),
                instant,
                OperationType.valueOf(operationType),
                request.getHeader(HttpHeaders.BANK_ID),
                request.getHeader(HttpHeaders.FINTECH_USER_ID),
                request.getHeader(HttpHeaders.FINTECH_REDIRECT_URL_OK),
                request.getHeader(HttpHeaders.FINTECH_REDIRECT_URL_NOK)
        );
    }

    public AisListTransactionsDataToSign mapToListTransactions(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return new AisListTransactionsDataToSign(
                UUID.fromString(xRequestId),
                instant,
                OperationType.valueOf(operationType),
                request.getHeader(HttpHeaders.BANK_ID),
                request.getHeader(HttpHeaders.FINTECH_USER_ID),
                request.getHeader(HttpHeaders.FINTECH_REDIRECT_URL_OK),
                request.getHeader(HttpHeaders.FINTECH_REDIRECT_URL_NOK),
                request.getParameter(QueryParams.DATE_FROM),
                request.getParameter(QueryParams.DATE_TO),
                request.getParameter(QueryParams.ENTRY_REFERENCE_FROM),
                request.getParameter(QueryParams.BOOKING_STATUS),
                request.getParameter(QueryParams.DELTA_LIST)
        );
    }

    public BankProfileDataToSign mapToBankProfile(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return new BankProfileDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType));
    }

    public BankSearchDataToSign mapToBankSearch(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return new BankSearchDataToSign(
                UUID.fromString(xRequestId),
                instant,
                OperationType.valueOf(operationType),
                request.getParameter(QueryParams.KEYWORD)
        );
    }

    public ConfirmConsentDataToSign mapToConfirmConsent(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return new ConfirmConsentDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType));
    }

    public PaymentInitiationDataToSign mapToPaymentInititation(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return new PaymentInitiationDataToSign(
                UUID.fromString(xRequestId),
                instant,
                OperationType.valueOf(operationType),
                request.getHeader(HttpHeaders.BANK_ID),
                request.getHeader(HttpHeaders.FINTECH_USER_ID),
                request.getHeader(HttpHeaders.FINTECH_REDIRECT_URL_OK),
                request.getHeader(HttpHeaders.FINTECH_REDIRECT_URL_NOK)
        );
    }

    public PaymentInfoDataToSign mapToPaymentInfo(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return new PaymentInfoDataToSign(
                UUID.fromString(xRequestId),
                instant,
                OperationType.valueOf(operationType),
                request.getHeader(HttpHeaders.BANK_ID),
                request.getHeader(HttpHeaders.FINTECH_USER_ID)
        );
    }
}
