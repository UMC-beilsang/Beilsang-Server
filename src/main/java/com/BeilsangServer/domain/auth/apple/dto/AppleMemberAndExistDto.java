package com.BeilsangServer.domain.auth.apple.dto;

import com.BeilsangServer.domain.member.entity.Member;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppleMemberAndExistDto {
    private Member member;
    private Boolean existMember;
}
