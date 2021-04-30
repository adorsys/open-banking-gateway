package de.adorsys.opba.protocol.xs2a.util.logresolver.domain.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.opba.protocol.xs2a.util.logresolver.domain.NotSensitiveData;
import de.adorsys.xs2a.adapter.api.ResponseHeaders;
import lombok.Data;
import lombok.SneakyThrows;


@Data
public class ResponseLog<T> implements NotSensitiveData {

    private int statusCode;
    private T body;
    private ResponseHeaders headers;

    public String getNotSensitiveData() {
        return "ResponseLog("
                + "statusCode=" + this.statusCode
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String headersJson = mapper.writeValueAsString(headers);
        String bodyJson = mapper.writeValueAsString(body);

        return "ResponseLog{"
                + "statusCode=" + statusCode
                + ", headers=" + headersJson
                + ", body=" + bodyJson
                + '}';
    }
}
