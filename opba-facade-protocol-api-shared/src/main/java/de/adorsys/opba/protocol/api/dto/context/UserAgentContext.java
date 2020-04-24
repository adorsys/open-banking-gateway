package de.adorsys.opba.protocol.api.dto.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserAgentContext {

    /**
     * The IP address of PSUs browser, mobile phone, etc. (request originator).
     */
    private final String psuIpAddress;

    /**
     * The port of PSUs browser, mobile phone, etc. (request originator).
     */
    private final String psuIpPort;

    /**
     * Accepted MIME type by PSU. Currently application/json is implied.
     */
    private final String psuAccept;

    /**
     * PSUs' accepted charset. Currently UTF-8 implied.
     */
    private final String psuAcceptCharset;

    /**
     * PSUs' accepted encoding.
     */
    private final String psuAcceptEncoding;

    /**
     * PSUs' language of communication. Currently English is implied.
     */
    private final String psuAcceptLanguage;

    /**
     * The ID of PSUs' device (i.e. can be IMEI for mobile device)
     */
    private final String psuDeviceId;

    /**
     * PSUs' User-Agent string.
     */
    private final String psuUserAgent;

    /**
     * PSU's geo location.
     */
    private final String psuGeoLocation;
    private final String psuHttpMethod;

    @Builder.Default
    private Map<UserAgentExtras, Object> extras = new EnumMap<>(UserAgentExtras.class);
}
