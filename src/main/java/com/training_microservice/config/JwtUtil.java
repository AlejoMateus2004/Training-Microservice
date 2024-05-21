package com.training_microservice.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static String SECRET_KEY = "3pam_gyw";
    private static Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

    public boolean isValid(String jwt) {
        try {
            JWT.require(ALGORITHM)
                    .build()
                    .verify(jwt);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getUsername(String jwt) {
        return JWT.require(ALGORITHM)
                .build()
                .verify(jwt)
                .getSubject();
    }

    public String getRoles(String jwt) {
        return JWT.require(ALGORITHM)
                .build()
                .verify(jwt)
                .getClaim("ROLES").toString();
    }
}
