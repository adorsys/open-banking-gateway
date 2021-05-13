package de.adorsys.opba.protocol.facade.util.logresolver.domain.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import static de.adorsys.opba.protocol.api.Constants.NULL;


@Getter
@RequiredArgsConstructor
public class RequestLog<T extends FacadeServiceableGetter> implements NotSensitiveData {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private final T request;

    @Override
    public String getNotSensitiveData() {
        if (null == request) {
            return NULL;
        }

        return "RequestLog("
                + "requestClass=" + request.getClass()
                + ", requestId=" + (null != request.getFacadeServiceable() ? request.getFacadeServiceable().getRequestId() : NULL)
                + ", serviceSessionId=" + (null != request.getFacadeServiceable() ? request.getFacadeServiceable().getServiceSessionId() : NULL)
                + ", authorizationSessionId=" + (null != request.getFacadeServiceable() ? request.getFacadeServiceable().getAuthorizationSessionId() : NULL)
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {
        String json = MAPPER.writeValueAsString(request);

        return "RequestLog{"
                + ", requestClass=" + (null != request ? request.getClass() : NULL)
                + ", request=" + json
                + '}';
    }
}
