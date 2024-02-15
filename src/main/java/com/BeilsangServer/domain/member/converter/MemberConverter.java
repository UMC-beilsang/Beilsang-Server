package com.BeilsangServer.domain.member.converter;

import com.BeilsangServer.domain.member.dto.MemberResponseDTO;
import com.BeilsangServer.domain.member.entity.Member;

import java.util.List;

public class MemberConverter {

    public static MemberResponseDTO.MemberDTO toMemberDTO(Member member) {

        return MemberResponseDTO.MemberDTO.builder()
                .memberId(member.getId())
                .nickName(member.getNickName())
                .totalPoint(member.getPoint())
                .build();
    }

    public static MemberResponseDTO.profileDTO toProfileDTO(Member member){

        return MemberResponseDTO.profileDTO.builder()
                .nickName(member.getNickName())
                .birth(member.getBirth())
                .gender(member.getGender())
                .address(member.getAddress())
                .build();
    }

    public static MemberResponseDTO.CheckEnrolledDTO toCheckEnrolledDTO(Boolean isEnrolled, List<Long> enrolledChallengeIds) {
        return MemberResponseDTO.CheckEnrolledDTO.builder().isEnrolled(isEnrolled).enrolledChallengeIds(enrolledChallengeIds).build();
    }
}
