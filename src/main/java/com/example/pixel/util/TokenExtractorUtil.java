package com.example.pixel.util;

import com.example.pixel.exception.MissingTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class TokenExtractorUtil {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String MISSING_TOKEN_EXCEPTION_MESSAGE = "Token is not present in the request";

    public static String getTokenFromRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length());
        } else {
            throw new MissingTokenException(MISSING_TOKEN_EXCEPTION_MESSAGE);
        }
    }
}