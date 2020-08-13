package de.adorsys.opba.fintech.impl.controller.utils;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@ToString
public class RestRequestContext {
    String uri;
    String sessionCookieValue;
    String oauth2StateCookieValue;
    String redirectCookieValue;
    String xsrfTokenHeaderField;
    String requestId;
}
