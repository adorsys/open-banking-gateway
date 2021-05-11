package de.adorsys.opba.protocol.hbci.util.logresolver.domain.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.multibanking.domain.response.AbstractResponse;
import lombok.Data;
import lombok.SneakyThrows;


@Data
public class ResponseLog<T extends AbstractResponse> {

    private T response;

    public String getNotSensitiveData() {
        return "ResponseLog("
                + "responseClass=" + response.getClass()
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(response);

        return "ResponseLog{"
                + "responseClass=" + response.getClass()
                + ", response=" + json
                + '}';
    }
}
