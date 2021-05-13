package de.adorsys.opba.protocol.hbci.util.logresolver.domain.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.multibanking.domain.request.AbstractRequest;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import static de.adorsys.opba.protocol.api.Constants.NULL;


@Getter
@RequiredArgsConstructor
public class RequestLog<T extends AbstractRequest> implements NotSensitiveData {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final T request;

    @Override
    public String getNotSensitiveData() {
        if (null == request) {
            return NULL;
        }

        return "RequestLog("
                + "requestClass=" + request.getClass()
                + ", bankApi=" + (null != request.getBankApiUser() ? request.getBankApiUser().getBankApi() : NULL)
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {
        String json = MAPPER.writeValueAsString(request);

        return "RequestLog{"
                + "requestClass=" + (null != request ? request.getClass() : NULL)
                + ", request=" + json
                + '}';
    }
}
