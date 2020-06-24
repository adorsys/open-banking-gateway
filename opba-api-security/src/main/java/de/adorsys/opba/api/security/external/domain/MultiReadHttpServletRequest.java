package de.adorsys.opba.api.security.external.domain;

import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;

/**
 * This class decorates incoming HttpServletRequest and caches its body
 */
public class MultiReadHttpServletRequest extends ContentCachingRequestWrapper {

    /**
     * Create a new ContentCachingRequestWrapper for the given servlet request.
     *
     * @param request the original servlet request
     */
    public MultiReadHttpServletRequest(HttpServletRequest request) {
        super(request);
    }
}

