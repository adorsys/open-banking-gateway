package de.adorsys.opba.tppbankingapi;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
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
}
