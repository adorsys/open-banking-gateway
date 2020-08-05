package de.adorsys.opba.tppbankingapi.controller;

import de.adorsys.opba.api.security.external.service.RequestSigningService;
import de.adorsys.opba.api.security.generator.api.DataToSignProvider;
import de.adorsys.opba.api.security.generator.api.RequestToSign;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static de.adorsys.opba.api.security.external.domain.HttpHeaders.X_REQUEST_SIGNATURE;

@RequiredArgsConstructor
public class SignaturePostProcessor implements RequestPostProcessor {

    private final RequestSigningService signingService;
    private final DataToSignProvider dataToSignProvider;

    @Override
    @SneakyThrows
    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        RequestToSign toSign = RequestToSign.builder()
                .method(DataToSignProvider.HttpMethod.valueOf(request.getMethod()))
                .path(request.getRequestURI())
                .headers(extractHeaders(request))
                .queryParams(extractQueryParams(request))
                .body(request.getContentLength() > 0 ? request.getContentAsString() : null)
                .build();

        String signature = signingService.signature(dataToSignProvider.normalizerFor(toSign).canonicalStringToSign(toSign));
        request.addHeader(X_REQUEST_SIGNATURE, signature);
        return request;
    }

    private Map<String, String> extractHeaders(MockHttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String headerName = names.nextElement();
            result.put(headerName, request.getHeader(headerName));
        }
        return result;
    }

    private Map<String, String> extractQueryParams(MockHttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String parameterName = names.nextElement();
            result.put(parameterName, request.getParameter(parameterName));
        }
        return result;
    }
}