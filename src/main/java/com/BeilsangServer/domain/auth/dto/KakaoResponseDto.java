package com.BeilsangServer.domain.auth.dto;

import lombok.*;


//서버에서 만든 access token, refresh token과 existMember를 response 해 주는 Dto
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoResponseDto {
    private String accessToken;
    private String refreshToken;
    private boolean existMember;
}
