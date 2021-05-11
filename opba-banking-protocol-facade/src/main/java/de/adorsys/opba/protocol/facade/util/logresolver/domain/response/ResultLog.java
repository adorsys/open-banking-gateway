package de.adorsys.opba.protocol.facade.util.logresolver.domain.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import de.adorsys.opba.protocol.api.dto.result.fromprotocol.Result;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import static de.adorsys.opba.protocol.facade.util.logresolver.Constants.NULL;


@Getter
@RequiredArgsConstructor
public class ResultLog<RESULT extends Result> implements NotSensitiveData {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private final RESULT result;

    @Override
    public String getNotSensitiveData() {
        if (null == result) {
            return NULL;
        }

        return "ResultLog("
                + "resultClass=" + result.getClass()
                + ", authContext=" + result.authContext()
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {
        String json = MAPPER.writeValueAsString(result);

        return "ResultLog{"
                + "resultClass=" + (null != result ? result.getClass() : NULL)
                + ", result=" + json
                + '}';
    }
}
