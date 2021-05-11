package de.adorsys.opba.protocol.facade.util.logresolver.domain.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import static de.adorsys.opba.protocol.facade.util.logresolver.Constants.NULL;


@Getter
@RequiredArgsConstructor
public class FacadeServiceableRequestLog implements NotSensitiveData {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final FacadeServiceableRequest request;

    @Override
    public String getNotSensitiveData() {
        if (null == request) {
            return NULL;
        }

        return "FacadeServiceableRequestLog("
                + "requestClass=" + request.getClass()
                + ", requestId=" + request.getRequestId()
                + ", serviceSessionId=" + request.getServiceSessionId()
                + ", authorizationSessionId=" + request.getAuthorizationSessionId()
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {
        String json = MAPPER.writeValueAsString(request);

        return "FacadeServiceableRequestLog{"
                + "requestClass=" + (null != request ? request.getClass() : NULL)
                + ", request=" + json
                + '}';
    }
}
