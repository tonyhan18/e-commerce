package kr.hhplus.be.server.support.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingFilter extends OncePerRequestFilter{

    private final ObjectMapper objectMapper;

    public LoggingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            logRequest(requestWrapper);

            long duration = System.currentTimeMillis() - startTime;
            logResponse(responseWrapper, duration);

            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String ip = request.getRemoteAddr();
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String requestBody = getContent(request.getContentAsByteArray());

        log.info("[REQUEST] IP: {}, Method: {}, URL: {}, Body: {}", ip, method, url, requestBody);
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        int status = response.getStatus();
        String responseBody = getContent(response.getContentAsByteArray());

        log.info("[RESPONSE] Duration: {}ms, Status: {}, Body: {}", duration, status, responseBody);
    }

    private String getContent(byte[] content) {
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return "[EMPTY]";
    }
    
}
