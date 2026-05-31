package com.tuan.center.service.impl;

import com.tuan.center.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final long REFRESH_EXPIRE_DAYS = 7;

    @Override
    public void saveRefreshToken(
            Long userId,
            String tokenId,
            String refreshToken) {

        String key =
                "refresh_token:" + userId + ":" + tokenId;

        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                Duration.ofDays(REFRESH_EXPIRE_DAYS)
        );
    }

    @Override
    public String getRefreshToken(
            Long userId,
            String tokenId) {

        return redisTemplate.opsForValue().get(
                "refresh_token:" + userId + ":" + tokenId
        );
    }


    @Override
    public void deleteRefreshToken(
            Long userId,
            String tokenId) {

        redisTemplate.delete(
                "refresh_token:" + userId + ":" + tokenId
        );
    }
}
