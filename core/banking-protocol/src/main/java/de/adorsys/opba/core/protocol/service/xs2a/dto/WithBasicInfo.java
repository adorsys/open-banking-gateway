package de.adorsys.opba.core.protocol.service.xs2a.dto;

import com.google.common.net.MediaType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

import static de.adorsys.xs2a.adapter.service.RequestHeaders.CONTENT_TYPE;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_GTW_ASPSP_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_REQUEST_ID;

@Getter
@Setter
public class WithBasicInfo {

    @NotBlank(message = "{input.textbox.string.no.psu.id}")
    private String psuId;

    @NotBlank(message = "{input.textbox.string.no.aspsp.id}")
    private String aspspId;

    @NotBlank(message = "{input.textbox.string.no.request.id}")
    private String requestId;

    @NotBlank
    private String contentType = MediaType.JSON_UTF_8.type();

    public Map<String, String> asMap() {
        Map<String, String> allValues = new HashMap<>();
        allValues.put(PSU_ID, psuId);
        allValues.put(X_GTW_ASPSP_ID, aspspId);
        allValues.put(X_REQUEST_ID, requestId);
        allValues.put(CONTENT_TYPE, contentType);
        return allValues;
    }
}
