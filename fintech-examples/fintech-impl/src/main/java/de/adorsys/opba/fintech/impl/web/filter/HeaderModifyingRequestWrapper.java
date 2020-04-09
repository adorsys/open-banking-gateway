package de.adorsys.opba.fintech.impl.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

public class HeaderModifyingRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String> headerMap = new HashMap<>();

    public HeaderModifyingRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void addHeader(String name, String value) {
        headerMap.put(name, value);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        HttpServletRequest request = (HttpServletRequest) getRequest();
        List<String> list = new ArrayList<>();

        for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
            list.add(e.nextElement());
        }

        list.addAll(headerMap.keySet());

        return Collections.enumeration(list);
    }

    @Override
    public String getHeader(String name) {
        String value = headerMap.get(name);

        return value != null
                       ? value
                       : ((HttpServletRequest) getRequest()).getHeader(name);
    }
}
