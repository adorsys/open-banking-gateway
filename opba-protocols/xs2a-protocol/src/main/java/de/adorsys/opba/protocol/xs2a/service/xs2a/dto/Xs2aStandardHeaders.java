package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.protocol.xs2a.service.xs2a.context.Xs2aContext;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import lombok.Data;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_GTW_ASPSP_ID;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.X_REQUEST_ID;

@Data
public class Xs2aStandardHeaders {

    @NotBlank(message = "{no.psu.id}")
    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("psuId"))
    private String psuId;

    @NotBlank(message = "{no.aspsp.id}")
    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("aspspId"))
    private String aspspId;

    @NotBlank // can't be provided manually
    private String sagaId;

    public RequestHeaders toHeaders() {
        Map<String, String> allValues = new HashMap<>();

        allValues.put(PSU_ID, psuId);
        allValues.put(X_REQUEST_ID, sagaId);
        allValues.put(X_GTW_ASPSP_ID, aspspId);

        return RequestHeaders.fromMap(allValues);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE)
    public interface FromCtx extends DtoMapper<Xs2aContext, Xs2aStandardHeaders> {
        Xs2aStandardHeaders map(Xs2aContext ctx);
    }
}
