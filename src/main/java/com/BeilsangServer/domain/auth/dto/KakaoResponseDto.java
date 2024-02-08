package com.BeilsangServer.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;


//ios에서 받은 카카오 access token을 저장할 Response Dto
@Getter
@Setter
public class KakaoResponseDto {
    private String accessToken;
}
