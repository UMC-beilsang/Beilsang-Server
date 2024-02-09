package com.BeilsangServer.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
public class AppleMemberDto {
    private String socialId;
    private String email;

    @Builder
    private AppleMemberDto(String socialId, String email){
        this.socialId = socialId;
        this.email = email;
    }
}
