package com.BeilsangServer.domain.member.converter;

import com.BeilsangServer.domain.member.dto.MemberResponseDTO;
import com.BeilsangServer.domain.member.entity.Member;

public class MemberConverter {

    public static MemberResponseDTO.MemberDTO toMemberDTO(Member member) {

        return MemberResponseDTO.MemberDTO.builder()
                .memberId(member.getId())
                .nickName(member.getNickName())
                .totalPoint(member.getTotalPoint())
                .build();
    }
}