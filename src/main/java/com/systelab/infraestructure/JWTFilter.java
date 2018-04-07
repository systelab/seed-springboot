package com.systelab.infraestructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JWTFilter extends GenericFilterBean {

    private JWTAuthenticationTokenGenerator tokenGenerator;

    public JWTFilter(JWTAuthenticationTokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        final String authHeader = ((HttpServletRequest) req).getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            if (tokenGenerator==null) System.out.println("tokenGenerator is null");
            if (tokenGenerator.validateToken(authHeader.substring(7))) {
                Authentication authentication = tokenGenerator.getAuthentication(authHeader.substring(7));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(req, res);

    }

}

