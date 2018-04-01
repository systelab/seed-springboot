package com.systelab.infraestructure;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JWTAuthenticationTokenGenerator {

    @Value("${jwt.secret}")
    private String secret;

    public String issueToken(String username, String role, String uri) {
        Key key = generateKey();

        Claims customClaims = Jwts.claims();
        customClaims.put("role", role);

        return Jwts.builder().setClaims(customClaims).setSubject(username).setIssuer(uri).setIssuedAt(new Date()).setExpiration(toDate(LocalDateTime.now().plusMinutes(15L))).signWith(SignatureAlgorithm.HS512, key).compact();
    }

    public String validateToken(String token) throws Exception {
        Key key = generateKey();
        Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
        Claims c = claims.getBody();
        return c.get("role").toString();
    }

    public Key generateKey() {
        return new SecretKeySpec(secret.getBytes(), 0, secret.getBytes().length, "DES");
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}