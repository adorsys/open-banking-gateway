package de.adorsys.opba.core.protocol.service.xs2a.dto.consent;

import de.adorsys.opba.core.protocol.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.core.protocol.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.core.protocol.service.xs2a.context.Xs2aContext;
import de.adorsys.opba.core.protocol.service.xs2a.dto.WithBasicInfo;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import javax.validation.constraints.NotBlank;
import java.util.Map;

import static de.adorsys.xs2a.adapter.service.RequestHeaders.PSU_IP_ADDRESS;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_NOK_REDIRECT_URI;
import static de.adorsys.xs2a.adapter.service.RequestHeaders.TPP_REDIRECT_URI;

@Getter
@Setter
public class ConsentInitiateHeaders extends WithBasicInfo {

    public static final FromCtx XS2A_HEADERS = Mappers.getMapper(FromCtx.class);

    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("psuIpAddress"))
    @NotBlank(message = "{no.ctx.psuIpAddress}")
    private String psuIpAddress;

    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("redirectUriOk"))
    @NotBlank(message = "{redirect.ok}")
    private String redirectUriOk;

    @ValidationInfo(ui = @FrontendCode("textbox.string"), ctx = @ContextCode("redirectUriNok"))
    @NotBlank(message = "{redirect.nok}")
    private String redirectUriNok;

    public RequestHeaders toHeaders() {
        Map<String, String> headers = super.asMap();
        headers.put(PSU_IP_ADDRESS, psuIpAddress);
        headers.put(TPP_REDIRECT_URI, redirectUriOk);
        headers.put(TPP_NOK_REDIRECT_URI, redirectUriNok);
        return RequestHeaders.fromMap(headers);
    }

    @Mapper
    public interface FromCtx {
        ConsentInitiateHeaders map(Xs2aContext ctx);
    }
}
