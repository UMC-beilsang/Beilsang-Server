package com.BeilsangServer.domain.member.service;


import com.BeilsangServer.domain.member.dto.MemberDto;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;


    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }


}