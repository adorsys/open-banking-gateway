package de.adorsys.opba.core.protocol.service.xs2a.dto;

import com.google.common.net.MediaType;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.ValidationInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

import static de.adorsys.xs2a.adapter.service.RequestHeaders.CONTENT_TYPE;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_GTW_BANK_CODE;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_REQUEST_ID;

@Getter
@Setter
public class WithBasicInfo {

    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("psuId"))
    @NotBlank(message = "{no.ctx.psuId}")
    private String psuId;

    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("aspspId"))
    @NotBlank(message = "{no.ctx.aspspId}")
    private String aspspId;

    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("requestId"))
    @NotBlank(message = "{no.ctx.requestId}")
    private String requestId;

    @NotBlank
    private String contentType = MediaType.JSON_UTF_8.type();

    public Map<String, String> asMap() {
        Map<String, String> allValues = new HashMap<>();
        allValues.put(PSU_ID, psuId);
        allValues.put(X_GTW_BANK_CODE, aspspId);
        allValues.put(X_REQUEST_ID, requestId);
        allValues.put(CONTENT_TYPE, contentType);
        return allValues;
    }
}
