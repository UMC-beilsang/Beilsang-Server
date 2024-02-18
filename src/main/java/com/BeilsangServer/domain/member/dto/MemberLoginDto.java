package com.BeilsangServer.domain.member.dto;

import com.BeilsangServer.global.enums.Category;
import com.BeilsangServer.global.enums.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberLoginDto {
    private String accessToken;
    private Gender gender;
    private String nickName;
    private String birth;
    private String address;
    private Category keyword;
    private String discoveredPath;
    private String resolution;
    private String recommendNickname;
}

