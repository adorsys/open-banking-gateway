package de.adorsys.opba.api.security.generator.api;

public interface RequestDataToSignGenerator {

    String canonicalStringToSign(RequestToSign toSign);
}
