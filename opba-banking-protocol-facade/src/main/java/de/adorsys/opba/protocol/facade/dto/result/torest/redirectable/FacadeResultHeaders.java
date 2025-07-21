package de.adorsys.opba.protocol.facade.dto.result.torest.redirectable;

import de.adorsys.opba.protocol.api.dto.result.body.AuthStateBody;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class FacadeResultHeaders<T> extends FacadeRedirectResult<T, AuthStateBody>  {
    private UUID xRequestId;
    private String serviceSessionId;

    private Map<String, String> headers = new HashMap<>();

    public static <T> FacadeResultHeaders<T> of(
            URI location,
            String setCookie,
            UUID xRequestId,
            String serviceSessionId
    ) {
        FacadeResultHeaders<T> result = new FacadeResultHeaders<>();
        result.setRedirectionTo(location);
        result.getHeaders().put("Location", location.toString());
        result.getHeaders().put("Set-Cookie", setCookie);
        result.setXRequestId(xRequestId);
        result.setServiceSessionId(serviceSessionId);
        return result;
    }
}
