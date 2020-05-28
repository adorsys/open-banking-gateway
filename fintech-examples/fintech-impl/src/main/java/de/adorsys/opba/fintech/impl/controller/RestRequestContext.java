package de.adorsys.opba.fintech.impl.controller;


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
    String sessionCookieValue;
    String redirectCookieValue;
    String xsrfTokenHeaderField;
    String requestId;
    String uri;
}
