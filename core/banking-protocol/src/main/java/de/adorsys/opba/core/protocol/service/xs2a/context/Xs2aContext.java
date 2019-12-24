package de.adorsys.opba.core.protocol.service.xs2a.context;

import de.adorsys.xs2a.adapter.service.RequestHeaders;
import de.adorsys.xs2a.adapter.service.model.StartScaProcessResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static de.adorsys.xs2a.adapter.service.RequestHeaders.CONSENT_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.CONTENT_TYPE;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_IP_ADDRESS;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_NOK_REDIRECT_URI;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_REDIRECT_URI;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_GTW_ASPSP_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_REQUEST_ID;

// TODO - Make immutable, modify only with toBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Xs2aContext extends BaseContext {

    // Application required
    private long bankConfigId = 1;
    // Redirect-required:
    private String executionId;

    // Mandatory static
    private String psuId;
    private String requestId;
    private String contentType = "application/json";
    private String aspspId;

    // Mandatory dynamic
    private String psuIpAddress;

    // Optional consent-specific
    private boolean withBalance;

    // In-process
    private String aspspScaApproach;
    private StartScaProcessResponse startScaProcessResponse;
    private String consentId;
    private String authorizationId;

    private String redirectUriOk;
    private String redirectUriNok;

    private Object result;

    public <T> T getResult() {
        return (T) result;
    }

    public <T> T getResult(Class<T> clazz) {
        return (T) result;
    }

    public RequestHeaders toHeaders() {
        Map<String, String> allValues = new HashMap<>();
        allValues.put(PSU_ID, psuId);
        allValues.put(X_REQUEST_ID, requestId);
        allValues.put(CONTENT_TYPE, contentType);
        allValues.put(X_GTW_ASPSP_ID, aspspId);
        allValues.put(TPP_REDIRECT_URI, redirectUriOk);
        allValues.put(TPP_NOK_REDIRECT_URI, redirectUriNok);

        allValues.put(PSU_IP_ADDRESS, psuIpAddress);

        allValues.put(CONSENT_ID, consentId);

        return RequestHeaders.fromMap(
                allValues.entrySet().stream()
                        .filter(it -> null != it.getValue())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
}
