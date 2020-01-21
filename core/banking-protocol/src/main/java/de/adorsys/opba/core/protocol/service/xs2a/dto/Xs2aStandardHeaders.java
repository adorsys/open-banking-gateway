package de.adorsys.opba.core.protocol.service.xs2a.dto;

import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import lombok.Data;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.Map;

import static de.adorsys.opba.core.protocol.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.core.protocol.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_GTW_ASPSP_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_REQUEST_ID;

@Data
public class Xs2aStandardHeaders {

    private String psuId;
    private String sagaId;
    private String aspspId;

    public RequestHeaders toHeaders() {
        Map<String, String> allValues = new HashMap<>();

        allValues.put(PSU_ID, psuId);
        allValues.put(X_REQUEST_ID, sagaId);
        allValues.put(X_GTW_ASPSP_ID, aspspId);

        return RequestHeaders.fromMap(allValues);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx {
        Xs2aStandardHeaders map(Xs2aContext ctx);
    }
}
