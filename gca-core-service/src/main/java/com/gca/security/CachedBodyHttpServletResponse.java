package com.gca.security;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream cachedBody = new ByteArrayOutputStream();
    private final ServletOutputStream outputStream = new CachedBodyServletOutputStream(cachedBody);
    private final PrintWriter writer = new PrintWriter(new OutputStreamWriter(cachedBody, StandardCharsets.UTF_8));

    public CachedBodyHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    public byte[] getBodyBytes() {
        return cachedBody.toByteArray();
    }

    private static class CachedBodyServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream buffer;

        public CachedBodyServletOutputStream(ByteArrayOutputStream buffer) {
            this.buffer = buffer;
        }

        @Override
        public void write(int b) {
            buffer.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            throw new UnsupportedOperationException();
        }
    }
}