package de.adorsys.opba.protocol.hbci.util.logresolver.domain.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.multibanking.domain.request.AbstractRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;


@Getter
@RequiredArgsConstructor
public class RequestLog<T extends AbstractRequest> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final T request;

    public String getNotSensitiveData() {
        if (null == request) {
            return "null";
        }

        return "RequestLog("
                + "requestClass=" + request.getClass()
                + "bankApi=" + (null != request.getBankApiUser() ? request.getBankApiUser().getBankApi() : "null")
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {
        String json = MAPPER.writeValueAsString(request);

        return "RequestLog{"
                + "requestClass=" + request.getClass()
                + "request=" + json
                + '}';
    }
}
