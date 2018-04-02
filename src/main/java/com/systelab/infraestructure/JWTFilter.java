package com.systelab.infraestructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JWTFilter implements Filter {

    @Autowired
    private JWTAuthenticationTokenGenerator tokenGenerator;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        final String authHeader = request.getHeader("Authorization");


        if (request.getRequestURL().toString().endsWith("/users/login")
                || request.getRequestURL().toString().endsWith("swagger-ui.html")
                || request.getRequestURL().toString().endsWith("v2/api-docs")
                || request.getRequestURL().toString().contains("swagger-resources")
                || request.getRequestURL().toString().contains("webjars")) {
            chain.doFilter(req, res);
        } else {
            if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
                chain.doFilter(req, res);
            } else {

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                final String token = authHeader.substring(7);
                try {
                    String userRole = tokenGenerator.validateToken(token);
                } catch (Exception ex) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                chain.doFilter(req, res);
            }
        }
    }

    @Override
    public void destroy() {

    }
}

