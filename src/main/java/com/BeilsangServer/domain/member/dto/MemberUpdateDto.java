package com.BeilsangServer.domain.member.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDto {

    private String nickName;
    private String birth;
    private String gender;
    private String address;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileImageDTO{
        private MultipartFile profileImage;
    }
}
