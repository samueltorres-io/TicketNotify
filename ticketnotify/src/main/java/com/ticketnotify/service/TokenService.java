package com.ticketnotify.service;

import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ticketnotify.entity.User;
import com.ticketnotify.exception.AppException;
import com.ticketnotify.exception.ErrorCode;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long accessTokenExpirationMs;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationMs;

    private static final String ISSUER = "ticketnotify";

    // Access Token
    public String generateAccessToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getId().toString())
                .withArrayClaim("roles", user.getRoles().stream()
                    .map(role -> role.getName())
                    .toArray(String[]::new))
                .withExpiresAt(genExpirationDate(accessTokenExpirationMs))
                .sign(algorithm);

        } catch (JWTCreationException e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String validateAccessToken(String token) {
        try {
            return verifyToken(token).getSubject();
        } catch (JWTVerificationException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID, HttpStatus.UNAUTHORIZED);
        }
    }

    // Refresh Token
    public String generateRefreshToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getId().toString())
                .withExpiresAt(genExpirationDate(refreshTokenExpirationMs))
                .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String validateRefreshToken(String token) {
        try {
            /* Depois do redis */
            return verifyToken(token).getSubject();
        } catch (JWTVerificationException e) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED, HttpStatus.FORBIDDEN);
        }
    }

    // Utils

    private DecodedJWT verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
            .withIssuer(ISSUER)
            .build()
            .verify(token);
    }

    private Date genExpirationDate(Long expirationMs) {
        return Date.from(Instant.now().plus(expirationMs, ChronoUnit.MILLIS));
    }

}
