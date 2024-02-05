package com.BeilsangServer.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;


@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class KakaoInfoDto {

    private Long id;

    public KakaoInfoDto(Map<String, Object> attributes) {
        this.id = Long.valueOf(attributes.get("id").toString());
    }

}
