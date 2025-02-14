package com.example.vannc.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenService {
    private final StringRedisTemplate redisTemplate;

    public TokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Lưu refresh token vào Redis với thời gian sống (TTL)
    public void storeRefreshToken(String userId, String refreshToken, long expirationMinutes) {
        redisTemplate.opsForValue().set("refreshToken:" + userId, refreshToken, Duration.ofMinutes(expirationMinutes));
    }

    // Lấy refresh token từ Redis
    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get("refreshToken:" + userId);
    }

    // Xóa refresh token khi logout
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete("refreshToken:" + userId);
    }
}
