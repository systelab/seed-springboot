package com.systelab.seed.infrastructure.security.authentication.service.filter;

import com.systelab.seed.infrastructure.security.config.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_PREFIX = "Bearer ";

    @Resource(name = "userManagementService")
    private UserDetailsService userDetailsService;

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            String authToken = header.replace(TOKEN_PREFIX, "");
            this.checkToken(authToken, new WebAuthenticationDetailsSource().buildDetails(req));
        } else {
            logger.warn("Couldn't find bearer. Header will be ignore.");
        }
        chain.doFilter(req, res);
    }

    private void checkToken(String authToken, WebAuthenticationDetails details) {
        try {
            Optional<String> username = tokenProvider.getUsernameFromToken(authToken);
            if (username.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username.get());
                if (tokenProvider.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = tokenProvider.getAuthentication(authToken, userDetails);
                    authentication.setDetails(details);
                    logger.info("Authenticated user " + username.get() + " with security context " + userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (IllegalArgumentException e) {
            logger.error("An error occurred during while getting username from token.", e);
        } catch (ExpiredJwtException e) {
            logger.warn("The token is expired and not valid anymore.", e);
        } catch (SignatureException e) {
            logger.error("Authentication Failed. Username or Password not valid.");
        }
    }
}