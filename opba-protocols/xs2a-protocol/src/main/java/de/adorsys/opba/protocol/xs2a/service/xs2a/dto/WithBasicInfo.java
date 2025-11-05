package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.xs2a.adapter.api.http.ContentType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.STRING;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.ACCEPT;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.AUTHORIZATION;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.CONTENT_TYPE;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.PSU_ID;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.X_GTW_ASPSP_ID;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.X_REQUEST_ID;

/**
 * Typical headers that are required for all XS2A-adapter api calls.
 */
@Getter
@Setter
public class WithBasicInfo {

    /**
     * PSU ID - PSU login in ASPSP API.
     */
    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(FieldCode.PSU_ID))
    private String psuId;

    /**
     * ASPSP ID - bank ID to be used with Xs2a adapter.
     */
    @NotBlank(message = "{no.ctx.aspspId}")
    private String aspspId;

    /**
     * X-Request-ID - request ID used for tracing.
     */
    @NotBlank(message = "{no.ctx.requestId}")
    private String requestId;

    @NotBlank
    private String contentType = ContentType.APPLICATION_JSON;

    @NotBlank
    private String accept = ContentType.ALL;

    // TODO: Validation - it should be present only for OAuth2
    private String oauth2Token;

    public Map<String, String> asMap() {
        Map<String, String> allValues = new HashMap<>();
        if (null != psuId) {
            allValues.put(PSU_ID, psuId);
        }
        allValues.put(X_GTW_ASPSP_ID, aspspId);
        allValues.put(X_REQUEST_ID, requestId);
        allValues.put(CONTENT_TYPE, contentType);
        allValues.put(ACCEPT, accept);

        if (null != oauth2Token) {
            allValues.put(AUTHORIZATION, oauth2Token);
        }

        return allValues;
    }
}
