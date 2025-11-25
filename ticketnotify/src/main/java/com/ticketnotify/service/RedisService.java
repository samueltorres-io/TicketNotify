package com.ticketnotify.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Refresh Token: Whitelist - Só é válido se estiver no Redis
     * Access Token: Blacklist - É válido, menos no Redis (revogado)
    */

    // Refresh Tokens
    public void saveRefreshToken(String token, UUID userId, Long durationMs) {
        redisTemplate.opsForValue().set("rt:" + token, userId.toString(), Duration.ofMillis(durationMs));
    }

    public String getUserIdFromRefreshToken(String token) {
        return redisTemplate.opsForValue().get("rt:" + token);
    }

    public void deleteRefreshToken(String token) {
        redisTemplate.delete("rt:" + token);
    }

    // Access Tokens
    public void addToBlackList(String token, Long remainingTimeMs) {
        redisTemplate.opsForValue().set("bl:" + token, "revoked", Duration.ofMillis(remainingTimeMs));
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("bl:" + token));
    }

}
