package com.BeilsangServer.domain.auth.service;


import com.BeilsangServer.domain.auth.dto.KakaoMemberAndExistDto;
import com.BeilsangServer.domain.auth.dto.KakaoResponseDto;
import com.BeilsangServer.domain.auth.util.SecurityUtil;
import com.BeilsangServer.domain.member.dto.MemberLoginDto;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.BeilsangServer.global.jwt.JwtTokenProvider;
import com.BeilsangServer.global.jwt.exception.CustomException;
import com.BeilsangServer.global.jwt.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoAuthService kakaoAuthService;

    //카카오 로그인
    public  KakaoResponseDto loginWithKakao(String accessToken, HttpServletResponse response) {
        KakaoMemberAndExistDto member = kakaoAuthService.getUserProfileByToken(accessToken);
        return getTokens(member.getMember().getSocialId(),member.getExistMember(), response);
    }

    @Transactional
    public void signup(MemberLoginDto memberLoginDto){

        Long memberId = SecurityUtil.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new RuntimeException("없는 멤버임."));
        member.setMemberInfo(memberLoginDto);

    }

    //Access Token, Refresh Token 생성
    @Transactional
    public KakaoResponseDto getTokens(Long id, Boolean existMember, HttpServletResponse response) {
        final String accessToken = jwtTokenProvider.createAccessToken(id.toString());
        final String refreshToken = jwtTokenProvider.createRefreshToken();

        Member member = memberRepository.findBySocialId(id);
        member.setRefreshToken(refreshToken);


        jwtTokenProvider.addRefreshTokenToCookie(refreshToken,response);

        return KakaoResponseDto.builder()
                .accessToken(accessToken)
                .existMember(existMember)
                .build();
    }

    // 리프레시 토큰으로 액세스토큰 새로 갱신
    public String refreshAccessToken(String refreshToken) {
        //유효성 검사 실패 시
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Member member = memberRepository.findByRefreshToken(refreshToken);
        if(member == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String changeRefreshToken = jwtTokenProvider.createAccessToken(member.getSocialId().toString());

        member.setRefreshToken(changeRefreshToken);

        return changeRefreshToken;
    }
}
