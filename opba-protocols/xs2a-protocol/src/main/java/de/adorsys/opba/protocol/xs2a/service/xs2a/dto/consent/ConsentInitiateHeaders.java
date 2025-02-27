package de.adorsys.opba.protocol.xs2a.service.xs2a.dto.consent;

import de.adorsys.opba.protocol.api.dto.codes.FieldCode;
import de.adorsys.opba.protocol.bpmnshared.dto.DtoMapper;
import de.adorsys.opba.protocol.xs2a.context.ais.Xs2aAisContext;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ContextCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.FrontendCode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.annotations.ValidationInfo;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.ResponseTokenMapper;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.ValidationMode;
import de.adorsys.opba.protocol.xs2a.service.xs2a.dto.WithBasicInfo;
import de.adorsys.xs2a.adapter.api.RequestHeaders;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

import static de.adorsys.opba.protocol.api.dto.codes.TypeCode.STRING;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.SPRING_KEYWORD;
import static de.adorsys.opba.protocol.xs2a.constant.GlobalConst.XS2A_MAPPERS_PACKAGE;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.PSU_IP_ADDRESS;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.PSU_IP_PORT;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.TPP_NOK_REDIRECT_URI;
import static de.adorsys.xs2a.adapter.api.RequestHeaders.TPP_REDIRECT_URI;

/**
 * Object that represents request Headers that are necessary to call ASPSP API for consent initiation.
 */
@Getter
@Setter
public class ConsentInitiateHeaders extends WithBasicInfo {

    /**
     * PSU IP address - IP address of PSU device/browser.
     */
    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(FieldCode.PSU_IP_ADDRESS))
    @NotBlank(message = "{no.ctx.psuIpAddress}")
    private String psuIpAddress;

    /**
     * ASPSP redirect URL to be called if consent was granted.
     */
    @NotBlank(message = "{redirect.ok}")
    private String redirectUriOk;

    /**
     * ASPSP redirect URL to be called if consent was declined.
     */
    @NotBlank(message = "{redirect.nok}")
    private String redirectUriNok;

    /**
     * IP port of IP address between PSU and TPP.
     */
    @ValidationInfo(ui = @FrontendCode(STRING), ctx = @ContextCode(FieldCode.PSU_IP_PORT), validationMode = ValidationMode.OPTIONAL)
    @NotBlank(message = "{no.ctx.psuIpPort}")
    private String psuIpPort;

    public RequestHeaders toHeaders() {
        Map<String, String> headers = super.asMap();
        addNotNullValue(headers, PSU_IP_ADDRESS, psuIpAddress);
        addNotNullValue(headers, TPP_REDIRECT_URI, redirectUriOk);
        addNotNullValue(headers, TPP_NOK_REDIRECT_URI, redirectUriNok);
        addNotNullValue(headers, PSU_IP_PORT, psuIpPort);

        return RequestHeaders.fromMap(headers);
    }

    @Mapper(componentModel = SPRING_KEYWORD, implementationPackage = XS2A_MAPPERS_PACKAGE, uses = ResponseTokenMapper.class)
    public interface FromAisCtx extends DtoMapper<Xs2aAisContext, ConsentInitiateHeaders> {

        ConsentInitiateHeaders map(Xs2aAisContext ctx);
    }

    public <K, V> Map<K, V> addNotNullValue(Map<K, V> map, K key, V value) {
        if (value != null) {
            map.put(key, value);
        }

        return map;
    }
}
