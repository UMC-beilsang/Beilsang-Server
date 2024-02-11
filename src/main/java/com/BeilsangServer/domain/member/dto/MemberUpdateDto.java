package com.BeilsangServer.domain.member.dto;

import com.BeilsangServer.domain.member.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDto {

    private String nickName;
    private LocalDate birth;
    private String gender;
    private String address;
}
