package com.shirt.pod.config;

import com.shirt.pod.model.entity.Log;
import com.shirt.pod.repository.LogRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {
    private final LogRepository logRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Bọc request và response
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        Log log = new Log();
        log.setMethod(request.getMethod());
        log.setUri(request.getRequestURI());
        log.setRequestParam(request.getQueryString());

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            String requestBody = getRequestBody(requestWrapper);
            log.setRequestPayload(requestBody);

            String responseBody = getResponseBody(responseWrapper);
            log.setResponsePayload(responseBody);

            log.setHttpCode(responseWrapper.getStatus());

            logRepository.save(log);

            // copy sang client sau đó mới quay lại try để trả về thằng controller
            responseWrapper.copyBodyToResponse();
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper requestWrapper) {
        if (isBinaryContent(requestWrapper.getContentType())) {
            return "[Binary Content]";
        }

        byte[] buf = requestWrapper.getContentAsByteArray();
        if (buf.length == 0)
            return null;
        try {
            String content = new String(buf, requestWrapper.getCharacterEncoding());
            return content.replace("\u0000", "");
        } catch (UnsupportedEncodingException e) {
            return "Unsupported Encoding";
        }
    }

    private String getResponseBody(ContentCachingResponseWrapper responseWrapper) {
        if (isBinaryContent(responseWrapper.getContentType())) {
            return "[Binary Content]";
        }

        byte[] buf = responseWrapper.getContentAsByteArray();
        if (buf.length == 0)
            return null;
        try {
            String content = new String(buf, responseWrapper.getCharacterEncoding());
            return content.replace("\u0000", "");
        } catch (UnsupportedEncodingException e) {
            return "Unsupported Encoding";
        }
    }

    private boolean isBinaryContent(String contentType) {
        if (contentType == null)
            return false;
        return contentType.startsWith("image/") ||
                contentType.startsWith("video/") ||
                contentType.startsWith("audio/") ||
                contentType.contains("pdf") ||
                contentType.contains("octet-stream");
    }
}
