package com.BeilsangServer.global.jwt.dto;

import com.BeilsangServer.domain.auth.entity.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {
    private TokenResDto tokenResDto;
    private RefreshToken refreshToken;
}