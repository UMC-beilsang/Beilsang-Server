package com.BeilsangServer.domain.auth.service;

import com.BeilsangServer.domain.auth.entity.token.TokenRepository;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.BeilsangServer.global.jwt.JwtTokenProvider;
import com.BeilsangServer.global.jwt.dto.MemberResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtProvider;
    private final TokenRepository tokenRepository;

    @Transactional
    public MemberResponseDto.TokenInfo kakaologin(Member kakaoInfo) {
        Member member = memberRepository.findByEmail(kakaoInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(UsernameNotFoundException.class.getName()));
        MemberResponseDto.TokenInfo tokenDto = jwtProvider.generateKakaoToken(kakaoInfo);

        log.error(member.getEmail()+" 로그인!");
        return tokenDto;
    }

    @Transactional
    public MemberResponseDto.TokenInfo appleLogin(Member appleInfo) {
        Member member = memberRepository.findByEmail(appleInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(UsernameNotFoundException.class.getName()));
        MemberResponseDto.TokenInfo tokenDto = jwtProvider.generateAppleToken(appleInfo);

        log.error(member.getEmail()+" 로그인!");
        return tokenDto;
    }




}