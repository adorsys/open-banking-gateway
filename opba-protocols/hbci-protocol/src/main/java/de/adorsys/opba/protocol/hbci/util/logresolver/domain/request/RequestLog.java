package de.adorsys.opba.protocol.hbci.util.logresolver.domain.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.multibanking.domain.request.AbstractRequest;
import lombok.Data;
import lombok.SneakyThrows;


@Data
public class RequestLog<T extends AbstractRequest> {

    private T request;

    public String getNotSensitiveData() {
        return "RequestLog("
                + "requestClass=" + request.getClass()
                + "bankApi=" + request.getBankApiUser().getBankApi()
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        return "RequestLog{"
                + "requestClass=" + request.getClass()
                + "request=" + json
                + '}';
    }
}
