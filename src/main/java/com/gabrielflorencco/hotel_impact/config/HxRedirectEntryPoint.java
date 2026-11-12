package com.gabrielflorencco.hotel_impact.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class HxRedirectEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public HxRedirectEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            throws java.io.IOException, jakarta.servlet.ServletException {
        if ("true".equals(request.getHeader("HX-Request"))) {
            response.setHeader("HX-Redirect", getLoginFormUrl());
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            return;
        }
        super.commence(request, response, authException);
    }
}