package com.BeilsangServer.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class KakaoInfoDto {
    private String socialId;
    private String email;

    public KakaoInfoDto(Map<String, Object> attributes) {
        this.socialId = attributes.get("id").toString();
        this.email = attributes.get("email") != null ? attributes.get("email").toString() : ""; //email이 null이 아닌 경우에만 저장
    }
}