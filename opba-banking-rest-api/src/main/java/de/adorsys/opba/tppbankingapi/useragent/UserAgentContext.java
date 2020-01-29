package de.adorsys.opba.tppbankingapi.useragent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class UserAgentContext {

    private final String psuIpAddress;
    private final String psuIpPort;
    private final String psuAccept;
    private final String psuAcceptCharset;
    private final String psuAcceptEncoding;
    private final String psuAcceptLanguage;
    private final String psuDeviceId;
    private final String psuUserAgent;
    private final String psuGeoLocation;
    private final String psuHttpMethod;

    @Builder.Default
    private Map<UserAgentExtras, Object> extras = new EnumMap<>(UserAgentExtras.class);
}
