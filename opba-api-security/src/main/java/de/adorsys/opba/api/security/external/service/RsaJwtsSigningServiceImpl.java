package de.adorsys.opba.api.security.external.service;

import de.adorsys.opba.api.security.external.domain.DataToSign;
import de.adorsys.opba.api.security.external.domain.HttpHeaders;
import de.adorsys.opba.api.security.external.domain.QueryParams;
import de.adorsys.opba.api.security.external.domain.signdata.AisListAccountsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.AisListTransactionsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankProfileDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankSearchDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.ConfirmConsentDataToSign;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RsaJwtsSigningServiceImpl implements RequestSigningService {
    private final PrivateKey privateKey;
    private final String signIssuer;
    private final String signSubject;
    private final SignatureAlgorithm algorithm;
    private final String claimNameKey;

    public RsaJwtsSigningServiceImpl(PrivateKey privateKey, String signIssuer, String signSubject, String claimNameKey) {
        this.privateKey = privateKey;
        this.signIssuer = signIssuer;
        this.signSubject = signSubject;
        this.claimNameKey = claimNameKey;
        this.algorithm = SignatureAlgorithm.forSigningKey(privateKey);
    }

    public RsaJwtsSigningServiceImpl(PrivateKey privateKey, String signIssuer, String signSubject, SignatureAlgorithm signatureAlgorithm, String claimNameKey) {
        this.privateKey = privateKey;
        this.signIssuer = signIssuer;
        this.signSubject = signSubject;
        this.algorithm = signatureAlgorithm;
        this.claimNameKey = claimNameKey;
    }

    @Override
    public String signature(AisListAccountsDataToSign dataToSign) {
        Map<String, String> values = new HashMap<>();
        values.put(HttpHeaders.BANK_ID, dataToSign.getBankId());
        values.put(HttpHeaders.FINTECH_USER_ID, dataToSign.getFintechUserId());
        values.put(HttpHeaders.FINTECH_REDIRECT_URL_OK, dataToSign.getRedirectOk());
        values.put(HttpHeaders.FINTECH_REDIRECT_URL_NOK, dataToSign.getRedirectNok());
        DataToSign data = new DataToSign(dataToSign.getXRequestId(), dataToSign.getInstant(), dataToSign.getOperationType(), values);

        return signature(data);
    }

    @Override
    public String signature(AisListTransactionsDataToSign dataToSign) {
        Map<String, String> values = new HashMap<>();
        values.put(HttpHeaders.BANK_ID, dataToSign.getBankId());
        values.put(HttpHeaders.FINTECH_USER_ID, dataToSign.getFintechUserId());
        values.put(HttpHeaders.FINTECH_REDIRECT_URL_OK, dataToSign.getRedirectOk());
        values.put(HttpHeaders.FINTECH_REDIRECT_URL_NOK, dataToSign.getRedirectNok());
        values.put(QueryParams.DATE_FROM, dataToSign.getDateFrom());
        values.put(QueryParams.DATE_TO, dataToSign.getDateTo());
        values.put(QueryParams.ENTRY_REFERENCE_FROM, dataToSign.getEntryReferenceFrom());
        values.put(QueryParams.BOOKING_STATUS, dataToSign.getBookingStatus());
        values.put(QueryParams.DELTA_LIST, dataToSign.getDeltaList());
        DataToSign data = new DataToSign(dataToSign.getXRequestId(), dataToSign.getInstant(), dataToSign.getOperationType(), values);

        return signature(data);
    }

    @Override
    public String signature(BankSearchDataToSign dataToSign) {
        Map<String, String> values = new HashMap<>();
        values.put(QueryParams.KEYWORD, dataToSign.getKeyword());
        DataToSign data = new DataToSign(dataToSign.getXRequestId(), dataToSign.getInstant(), dataToSign.getOperationType(), values);

        return signature(data);
    }

    @Override
    public String signature(BankProfileDataToSign dataToSign) {
        return signature(new DataToSign(
                dataToSign.getXRequestId(),
                dataToSign.getInstant(),
                dataToSign.getOperationType()
        ));
    }

    @Override
    public String signature(ConfirmConsentDataToSign dataToSign) {
        return signature(new DataToSign(
                dataToSign.getXRequestId(),
                dataToSign.getInstant(),
                dataToSign.getOperationType()
        ));
    }

    private String signature(DataToSign dataToSign) {
        return Jwts.builder()
                       .setSubject(signSubject)
                       .setIssuer(signIssuer)
                       .claim(claimNameKey, dataToSign.convertDataToString())
                       .signWith(privateKey, algorithm)
                       .compact();
    }
}
