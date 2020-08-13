package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Map;

import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.STRING;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_IP_ADDRESS;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_NOK_REDIRECT_URI;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_REDIRECT_URI;

/**
 * Object that represents request Headers that are necessary to call ASPSP API for consent initiation.
 */
@Getter
@Setter
public class InitiateHeaders extends WithBasicInfo {

    /**
     * PSU IP address - IP address of PSU device/browser.
     */
    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(FieldCode.PSU_IP_ADDRESS))
    @NotBlank(message = "{no.ctx.psuIpAddress}")
    private String psuIpAddress;

    /**
     * ASPSP redirect URL to be called if consent was granted.
     */
    @NotBlank(message = "{redirect.ok}")
    private String redirectUriOk;

    /**
     * ASPSP redirect URL to be called if consent was declined.
     */
    @NotBlank(message = "{redirect.nok}")
    private String redirectUriNok;

    public RequestHeaders toHeaders() {
        Map<String, String> headers = super.asMap();
        headers.put(PSU_IP_ADDRESS, psuIpAddress);
        headers.put(TPP_REDIRECT_URI, redirectUriOk);
        headers.put(TPP_NOK_REDIRECT_URI, redirectUriNok);
        return RequestHeaders.fromMap(headers);
    }
}
