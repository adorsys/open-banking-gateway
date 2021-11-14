package de.adorsys.opba.protocol.xs2a.service.xs2a.dto;

import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.api.dto.codes.TypeCode;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.Xs2aContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.xs2a.adapter.api.RequestHeaders;
import lombok.Data;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapper;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.AUTHORIZATION;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.PSU_ID;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.TPP_REDIRECT_PREFERRED;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.X_GTW_ASPSP_ID;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.X_REQUEST_ID;

/**
 * Standard headers to call XS2A-adapter (excluding consent creation cases).
 */
@Data
public class Xs2aStandardHeaders {

    /**
     * PSU ID - PSU login in ASPSP API.
     */
    @NotBlank(message = "{no.ctx.psuId}")
    @ValidationInfo(ui = @FrontendCode(TypeCode.STRING), ctx = @ContextCode(FieldCode.PSU_ID))
    private String psuId;

    /**
     * ASPSP ID - bank ID to be used with Xs2a adapter.
     */
    @NotBlank(message = "{no.aspsp.id}")
    private String aspspId;

    /**
     * X-Request-ID - request ID used for tracing.
     */
    @NotBlank // can't be provided manually
    private String requestId;


    /**
     *  URI for the Endpoint of the TPP-API to which the status of the payment initiation should be sent.
     */
    @Nullable
    private  String tppNotificationURI;

    /**
     * The string has the formstatus=X1, ..., Xn where Xi is one of the constants SCA, PROCESS, LAST and where constants are not repeated.
     */
    @Nullable
    private  String tppNotificationContentPreferred;


    /**
     * TPP-Redirect-Preferred - If value is null then approach is irrelevant for TPP.
     * If value is 'true' then Redirect approach is preferred for TPP, if 'false' then other approaches more preferred then Redirect.
     */
    @Nullable
    private Boolean tppRedirectPreferred;

    // TODO: Validation - it should be present only for OAuth2
    private String oauth2Token;

    public RequestHeaders toHeaders() {
        Map<String, String> allValues = new HashMap<>();

        allValues.put(PSU_ID, psuId);
        allValues.put(X_REQUEST_ID, requestId);
        allValues.put(X_GTW_ASPSP_ID, aspspId);

        if (tppRedirectPreferred != null) {
            allValues.put(TPP_REDIRECT_PREFERRED, String.valueOf(tppRedirectPreferred));
        }

        if (null != oauth2Token) {
            allValues.put(AUTHORIZATION, oauth2Token);
        }

        return RequestHeaders.fromMap(allValues);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE, uses = ResponseTokenMapper.class)
    public interface FromCtx extends DtoMapper<Xs2aContext, Xs2aStandardHeaders> {

        Xs2aStandardHeaders map(Xs2aContext ctx);
    }
}
