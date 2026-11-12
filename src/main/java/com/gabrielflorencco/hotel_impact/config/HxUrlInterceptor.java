package com.gabrielflorencco.hotel_impact.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class HxUrlInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        if ("true".equals(request.getHeader("HX-Request"))) {
            String uri = request.getRequestURI();
            String query = request.getQueryString();
            response.setHeader("HX-Push-Url", query == null ? uri : uri + "?" + query);
        }
    }
}