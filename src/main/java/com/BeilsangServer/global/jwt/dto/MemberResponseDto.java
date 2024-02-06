package com.BeilsangServer.global.jwt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class MemberResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class TokenInfo{

        @Schema(description="헤더",example="Bearer")
        private String grantType;

        @Schema(description="accesstoken",example="sadfoefpmewpfpaekfpafkepafkpafef...")
        private String accessToken;

        @Schema(description="access token 만료시간",example="60000")
        private Long accessTokenExpirationTime;

        @Schema(description="refreshtoken",example="sfdalfmadsladfmsldsfdfdsfm;a....")
        private String refreshToken;

        @Schema(description="refresh token 만료시간",example="60000")
        private Long refreshTokenExpirationTime;

    }
}
