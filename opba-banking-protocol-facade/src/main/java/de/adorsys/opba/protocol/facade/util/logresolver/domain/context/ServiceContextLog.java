package de.adorsys.opba.protocol.facade.util.logresolver.domain.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.opba.protocol.api.dto.NotSensitiveData;
import de.adorsys.opba.protocol.api.dto.context.Context;
import de.adorsys.opba.protocol.api.dto.request.FacadeServiceableGetter;
import de.adorsys.opba.protocol.facade.util.logresolver.domain.request.RequestLog;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import static de.adorsys.opba.protocol.api.Constants.NULL;


@Getter
@RequiredArgsConstructor
public class ServiceContextLog<REQUEST extends FacadeServiceableGetter> implements NotSensitiveData {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private final Context<REQUEST> ctx;

    @Override
    public String getNotSensitiveData() {
        if (null == ctx) {
            return NULL;
        }

        return "ServiceContextLog("
                + "contextClass=" + ctx.getClass()
                + ", serviceSessionId=" + ctx.getServiceSessionId()
                + ", authContext=" + ctx.getAuthContext()
                + ", " + ctx.loggableBankId()
                + ", " + getRequestNotSensetiveData()
                + ")";
    }

    @SneakyThrows
    @Override
    public String toString() {
        String json = MAPPER.writeValueAsString(ctx);

        return "ServiceContextLog{"
                + "contextClass=" + (null != ctx ? ctx.getClass() : NULL)
                + ", context=" + json
                + '}';
    }

    private String getRequestNotSensetiveData() {
        RequestLog<REQUEST> requestLog = new RequestLog<>(ctx.getRequest());
        return requestLog.getNotSensitiveData();
    }
}
