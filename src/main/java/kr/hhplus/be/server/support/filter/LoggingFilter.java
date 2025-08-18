package kr.hhplus.be.server.support.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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

    private static final String TRACE_ID = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String traceId = UUID.randomUUID().toString();
        responseWrapper.setHeader(TRACE_ID, traceId);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logRequestResponse(requestWrapper, responseWrapper, duration, traceId);

            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequestResponse(ContentCachingRequestWrapper request,
                                    ContentCachingResponseWrapper response,
                                    long duration,
                                    String traceId) {
        String ip = request.getRemoteAddr();
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String requestBody = getContent(request.getContentAsByteArray());
        int status = response.getStatus();
        String responseBody = getContent(response.getContentAsByteArray());

        log.info("[API] traceId: {}, ip: {}, method: {}, url: {}, status: {}, latency: {}ms request: {}, response: {}",
            traceId, ip, method, url, status, duration, requestBody, responseBody
        );
    }

    private String getContent(byte[] content) {
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return "[EMPTY]";
    }
}
