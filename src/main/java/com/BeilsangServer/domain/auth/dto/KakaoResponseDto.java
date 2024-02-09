package com.BeilsangServer.domain.auth.dto;

import lombok.*;


//ios에서 받은 카카오 access token을 저장할 Response Dto
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoResponseDto {
    private String accessToken;
    private boolean existMember;
}
