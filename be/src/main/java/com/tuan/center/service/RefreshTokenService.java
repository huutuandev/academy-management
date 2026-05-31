package com.tuan.center.service;

public interface RefreshTokenService {
    void saveRefreshToken(
            Long userId,
            String tokenId,
            String refreshToken);

    String getRefreshToken(
            Long userId,
            String tokenId);

    void deleteRefreshToken(
            Long userId,
            String tokenId);
}
