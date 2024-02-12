package com.BeilsangServer.domain.auth.apple.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
public class AppleMemberDto {
    private Long socialId;
    private String email;

    @Builder
    public AppleMemberDto(Long socialId, String email){
        this.socialId = socialId;
        this.email = email;
    }
}
