package de.adorsys.opba.protocol.hbci.util.logresolver.domain.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.multibanking.domain.response.AbstractResponse;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import static de.adorsys.opba.protocol.hbci.util.logresolver.Constants.NULL;


@Getter
@RequiredArgsConstructor
public class ResponseLog<T extends AbstractResponse> implements NotSensitiveData {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final T response;

    @Override
    public String getNotSensitiveData() {
        return "ResponseLog("
                + "responseClass=" + (null != response ? response.getClass() : NULL)
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {
        String json = MAPPER.writeValueAsString(response);

        return "ResponseLog{"
                + "responseClass=" + (null != response ? response.getClass() : NULL)
                + ", response=" + json
                + '}';
    }
}
