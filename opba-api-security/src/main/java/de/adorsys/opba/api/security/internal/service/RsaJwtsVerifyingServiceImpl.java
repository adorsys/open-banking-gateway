package de.adorsys.opba.api.security.internal.service;

import de.adorsys.opba.api.security.external.domain.DataToSign;
import de.adorsys.opba.api.security.external.domain.HttpHeaders;
import de.adorsys.opba.api.security.external.domain.QueryParams;
import de.adorsys.opba.api.security.external.domain.signdata.AisListAccountsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.AisListTransactionsDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankProfileDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.BankSearchDataToSign;
import de.adorsys.opba.api.security.external.domain.signdata.ConfirmConsentDataToSign;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class RsaJwtsVerifyingServiceImpl implements RequestVerifyingService {
    private final String claimNameKey;

    @Override
    public boolean verify(String signature, String encodedPublicKey, AisListAccountsDataToSign data) {
        Map<String, String> values = new HashMap<>();
        values.put(HttpHeaders.BANK_ID, data.getBankId());
        values.put(HttpHeaders.FINTECH_USER_ID, data.getFintechUserId());
        values.put(HttpHeaders.FINTECH_REDIRECT_URL_OK, data.getRedirectOk());
        values.put(HttpHeaders.FINTECH_REDIRECT_URL_NOK, data.getRedirectNok());
        DataToSign dataToSign = new DataToSign(data.getXRequestId(), data.getInstant(), data.getOperationType(), values);

        return verify(signature, encodedPublicKey, dataToSign);
    }

    @Override
    public boolean verify(String signature, String encodedPublicKey, AisListTransactionsDataToSign data) {
        Map<String, String> values = new HashMap<>();
        values.put(HttpHeaders.BANK_ID, data.getBankId());
        values.put(HttpHeaders.FINTECH_USER_ID, data.getFintechUserId());
        values.put(HttpHeaders.FINTECH_REDIRECT_URL_OK, data.getRedirectOk());
        values.put(HttpHeaders.FINTECH_REDIRECT_URL_NOK, data.getRedirectNok());
        values.put(QueryParams.DATE_FROM, data.getDateFrom());
        values.put(QueryParams.DATE_TO, data.getDateTo());
        values.put(QueryParams.ENTRY_REFERENCE_FROM, data.getEntryReferenceFrom());
        values.put(QueryParams.BOOKING_STATUS, data.getBookingStatus());
        values.put(QueryParams.DELTA_LIST, data.getDeltaList());
        DataToSign dataToSign = new DataToSign(data.getXRequestId(), data.getInstant(), data.getOperationType(), values);

        return verify(signature, encodedPublicKey, dataToSign);
    }

    @Override
    public boolean verify(String signature, String encodedPublicKey, BankSearchDataToSign data) {
        Map<String, String> values = new HashMap<>();
        values.put(QueryParams.KEYWORD, data.getKeyword());
        DataToSign dataToSign = new DataToSign(data.getXRequestId(), data.getInstant(), data.getOperationType(), values);

        return verify(signature, encodedPublicKey, dataToSign);
    }

    @Override
    public boolean verify(String signature, String encodedPublicKey, BankProfileDataToSign data) {
        return verify(signature, encodedPublicKey, new DataToSign(
                data.getXRequestId(),
                data.getInstant(),
                data.getOperationType()
        ));
    }

    @Override
    public boolean verify(String signature, String encodedPublicKey, ConfirmConsentDataToSign data) {
        return verify(signature, encodedPublicKey, new DataToSign(
                data.getXRequestId(),
                data.getInstant(),
                data.getOperationType()
        ));
    }

    private boolean verify(String signature, String encodedPublicKey, DataToSign dataToSign) {
        PublicKey publicKey = getRsaPublicKey(encodedPublicKey);

        if (publicKey == null) {
            return false;
        }

        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(publicKey).build();
            Claims claims = parser.parseClaimsJws(signature).getBody();

            return dataToSign.convertDataToString()
                           .equals(claims.get(claimNameKey));

        } catch (Exception e) {
            log.error("Signature verification error:  {} for signature {}", e.getMessage(), signature);
            return false;
        }
    }

    private PublicKey getRsaPublicKey(String encodedPublicKey) {
        try {
            X509EncodedKeySpec encodedPublicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(encodedPublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(encodedPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Encoded public key has wrong format :  {}", encodedPublicKey);
            return null;
        }
    }
}
