package com.BeilsangServer.domain.auth.dto;


import lombok.Getter;

// ios에서 받을 카카오 access token을 받기 위한 Request Dto
@Getter
public class KakaoRequestDto {
    private String accesstoken;
    private String deviceToken;
}
