package com.BeilsangServer.domain.auth.apple.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AppleAuthTokenResponse {
    String accessToken;
    Integer expiresIn;
    String idToken;
    String refreshToken;
    String tokenType;
}