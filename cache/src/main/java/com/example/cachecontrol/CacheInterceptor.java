package com.example.cachecontrol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CacheInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) {
        if (response.containsHeader(HttpHeaders.CACHE_CONTROL)) {
            return;
        }
        final String cacheControl = CacheControl
            .noCache()
            .cachePrivate()
            .getHeaderValue();
        response.addHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
    }

}
