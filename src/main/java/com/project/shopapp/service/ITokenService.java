package com.project.shopapp.service;

import com.project.shopapp.model.Token;
import com.project.shopapp.model.User;

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobileDevice);
    Token refreshToken(String refreshToken, User user) throws Exception;
}
