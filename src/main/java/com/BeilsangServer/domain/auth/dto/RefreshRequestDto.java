package com.BeilsangServer.domain.auth.dto;

import lombok.Getter;

@Getter
public class RefreshRequestDto {
    private String accessToken;
    private String refreshToken;
}
