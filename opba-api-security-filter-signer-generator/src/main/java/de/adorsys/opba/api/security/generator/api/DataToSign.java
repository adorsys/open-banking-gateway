package de.adorsys.opba.api.security.generator.api;

import lombok.Value;

import java.util.Map;

@Value
public class DataToSign {

    Map<String, String> headers;
    String path;
    Map<String, String> queryParams;
    String body;
}
