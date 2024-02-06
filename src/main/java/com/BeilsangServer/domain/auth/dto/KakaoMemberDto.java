package com.BeilsangServer.domain.auth.dto;


import lombok.*;


@Getter
@Builder
@NoArgsConstructor
public class KakaoMemberDto {
    private String socialId;
    private String email;

    @Builder
    private KakaoMemberDto(String socialId, String email){
       this.socialId = socialId;
       this.email = email;
    }
}

