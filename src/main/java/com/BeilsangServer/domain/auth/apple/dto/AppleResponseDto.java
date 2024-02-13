package com.BeilsangServer.domain.auth.apple.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class AppleResponseDto {

    private String accessToken;
    private String refreshToken;
    private Boolean existMember;
}