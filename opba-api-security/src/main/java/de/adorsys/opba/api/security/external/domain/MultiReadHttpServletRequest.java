package de.adorsys.opba.api.security.external.domain;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class decorates incoming HttpServletRequest, caches its body and is used by spring framework further
 */
public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {
    private ByteArrayOutputStream cachedBytes;

    public MultiReadHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (cachedBytes == null) {
            cacheInputStream();
        }

        return new CachedServletInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    private void cacheInputStream() throws IOException {
        cachedBytes = new ByteArrayOutputStream();
        IOUtils.copy(super.getInputStream(), cachedBytes);
    }

    private class CachedServletInputStream extends ServletInputStream {
        private ByteArrayInputStream input;

        CachedServletInputStream() {
            input = new ByteArrayInputStream(cachedBytes.toByteArray());
        }

        @Override
        public int read() {
            return input.read();
        }

        @Override
        public boolean isFinished() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isReady() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // TODO Auto-generated method stub
        }
    }
}

