package com.BeilsangServer.domain.auth.apple.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AppleAuthTokenResponse {
    String accessToken;
    Integer expiresIn;
    String idToken;
    String refreshToken;
    String tokenType;
}