package com.BeilsangServer.domain.member.dto;

import com.BeilsangServer.domain.member.entity.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class MemberLoginDto {

    private Gender gender;
    private String nickName;
    private LocalDate birth;
    private String address;
    private String keyword;
    private String discoveredPath;
    private String resolution;
    private String recommendNickname;
}
