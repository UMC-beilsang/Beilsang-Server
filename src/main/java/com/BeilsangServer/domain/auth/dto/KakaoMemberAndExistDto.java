package com.BeilsangServer.domain.auth.dto;

import com.BeilsangServer.domain.member.entity.Member;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoMemberAndExistDto {
    private Member member;
    private Boolean existMember;
}
