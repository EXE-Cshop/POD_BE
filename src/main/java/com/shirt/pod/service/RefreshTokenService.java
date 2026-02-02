package com.shirt.pod.service;

import com.shirt.pod.model.entity.RefreshToken;
import com.shirt.pod.model.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);

    RefreshToken verifyExpiration(RefreshToken token);

    void deleteByUser(User user);

    RefreshToken findByToken(String token);

    void deleteRefreshToken(RefreshToken token);
}
