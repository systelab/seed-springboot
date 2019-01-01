package com.systelab.seed.config.authentication;

import com.systelab.seed.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;


@Configuration
public class TokenProvider implements Serializable {

    public static final String AUTHORITIES_KEY = "scopes";

    private final JwtConfig properties;

    @Autowired
    public TokenProvider(JwtConfig properties) {
        this.properties = properties;
    }

    public Optional<String> getUsernameFromToken(String token) {
        return Optional.ofNullable(getAllClaimsFromToken(token).getSubject());
    }

    private Optional<Date> getExpirationDateFromToken(String token) {
        return Optional.ofNullable(getAllClaimsFromToken(token).getExpiration());
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(properties.getClientSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token)
                .map(expiration -> expiration.before(new Date()))
                .orElse(true);
    }

    public String generateToken(Authentication authentication) {
        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS256, properties.getClientSecret())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + properties.getTokenValiditySeconds() * 1000))
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        return getUsernameFromToken(token)
                .map(username -> username.equals(userDetails.getUsername()) && !isTokenExpired(token))
                .orElse(false);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth, final UserDetails userDetails) {
        final Claims claims = getAllClaimsFromToken(token);
        final Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

}
