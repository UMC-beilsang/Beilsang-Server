package com.BeilsangServer.global.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResDto {

    private String grantType;
    private String accessToken;
    private Long accessTokenExpiresIn;
}