package com.globalogic.demo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    public static String JWT_SECRET = "G1.8A";

    public static long JWT_EXPIRATION_TIME = 300000;

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public String createToken(String subject) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(JWT_SECRET.getBytes()));
    }

    public String extractSubject(String token) {
        return JWT.require(Algorithm.HMAC512(JWT_SECRET.getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();
    }

}
