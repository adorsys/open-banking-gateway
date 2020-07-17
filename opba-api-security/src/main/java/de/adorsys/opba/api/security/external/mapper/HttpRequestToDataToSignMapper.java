package de.adorsys.opba.api.security.external.mapper;

import com.google.common.io.CharStreams;
import de.adorsys.opba.api.security.external.domain.HttpHeaders;
import de.adorsys.opba.api.security.external.domain.OperationType;
import de.adorsys.opba.api.security.external.domain.QueryParams;
import de.adorsys.opba.api.security.external.domain.signdata.AisListAccountsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.AisListTransactionsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankProfileDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankSearchDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.ConfirmConsentDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.ConfirmPaymentDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.GetPaymentDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.GetPaymentStatusDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.PaymentInitiationDataToSign;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.UUID;

public class HttpRequestToDataToSignMapper {

    public AisListAccountsDataToSign mapToListAccounts(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return AisListAccountsDataToSign.builder()
                       .xRequestId(UUID.fromString(xRequestId))
                       .instant(instant)
                       .operationType(OperationType.valueOf(operationType))
                       .bankId(request.getHeader(HttpHeaders.BANK_ID))
                       .fintechUserId(request.getHeader(HttpHeaders.FINTECH_USER_ID))
                       .redirectOk(request.getHeader(HttpHeaders.FINTECH_REDIRECT_URL_OK))
                       .redirectNok(request.getHeader(HttpHeaders.FINTECH_REDIRECT_URL_NOK))
                       .build();
    }

    public AisListTransactionsDataToSign mapToListTransactions(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return AisListTransactionsDataToSign.builder()
                       .xRequestId(UUID.fromString(xRequestId))
                       .instant(instant)
                       .operationType(OperationType.valueOf(operationType))
                       .bankId(request.getHeader(HttpHeaders.BANK_ID))
                       .fintechUserId(request.getHeader(HttpHeaders.FINTECH_USER_ID))
                       .redirectOk(request.getHeader(HttpHeaders.FINTECH_REDIRECT_URL_OK))
                       .redirectNok(request.getHeader(HttpHeaders.FINTECH_REDIRECT_URL_NOK))
                       .dateFrom(request.getParameter(QueryParams.DATE_FROM))
                       .dateTo(request.getParameter(QueryParams.DATE_TO))
                       .entryReferenceFrom(request.getParameter(QueryParams.ENTRY_REFERENCE_FROM))
                       .bookingStatus(request.getParameter(QueryParams.BOOKING_STATUS))
                       .deltaList(request.getParameter(QueryParams.DELTA_LIST))
                       .build();
    }

    public BankProfileDataToSign mapToBankProfile(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return new BankProfileDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType));
    }

    public BankSearchDataToSign mapToBankSearch(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return BankSearchDataToSign.builder()
                       .xRequestId(UUID.fromString(xRequestId))
                       .instant(instant)
                       .operationType(OperationType.valueOf(operationType))
                       .keyword(request.getParameter(QueryParams.KEYWORD))
                       .build();
    }

    public ConfirmConsentDataToSign mapToConfirmConsent(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return new ConfirmConsentDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType));
    }

    public ConfirmPaymentDataToSign mapToConfirmPayment(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return new ConfirmPaymentDataToSign(UUID.fromString(xRequestId), instant, OperationType.valueOf(operationType));
    }

    @SneakyThrows
    public PaymentInitiationDataToSign mapToPaymentInititation(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return PaymentInitiationDataToSign.builder()
                       .xRequestId(UUID.fromString(xRequestId))
                       .instant(instant)
                       .operationType(OperationType.valueOf(operationType))
                       .bankId(request.getHeader(HttpHeaders.BANK_ID))
                       .fintechUserId(request.getHeader(HttpHeaders.FINTECH_USER_ID))
                       .redirectOk(request.getHeader(HttpHeaders.FINTECH_REDIRECT_URL_OK))
                       .redirectNok(request.getHeader(HttpHeaders.FINTECH_REDIRECT_URL_NOK))
                       .psuAuthenticationRequired(request.getHeader(HttpHeaders.X_PIS_PSU_AUTHENTICATION_REQUIRED))
                       .body(CharStreams.toString(request.getReader()))
                       .build();
    }

    public GetPaymentDataToSign mapToGetPayment(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return GetPaymentDataToSign.builder()
                       .xRequestId(UUID.fromString(xRequestId))
                       .instant(instant)
                       .operationType(OperationType.valueOf(operationType))
                       .bankId(request.getHeader(HttpHeaders.BANK_ID))
                       .fintechUserId(request.getHeader(HttpHeaders.FINTECH_USER_ID))
                       .build();
    }

    public GetPaymentStatusDataToSign mapToGetPaymentStatus(HttpServletRequest request, Instant instant) {
        String xRequestId = request.getHeader(HttpHeaders.X_REQUEST_ID);
        String operationType = request.getHeader(HttpHeaders.X_OPERATION_TYPE);

        return GetPaymentStatusDataToSign.builder()
                       .xRequestId(UUID.fromString(xRequestId))
                       .instant(instant)
                       .operationType(OperationType.valueOf(operationType))
                       .bankId(request.getHeader(HttpHeaders.BANK_ID))
                       .fintechUserId(request.getHeader(HttpHeaders.FINTECH_USER_ID))
                       .build();
    }
}
