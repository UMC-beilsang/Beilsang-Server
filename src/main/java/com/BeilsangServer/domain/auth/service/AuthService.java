package com.BeilsangServer.domain.auth.service;


import com.BeilsangServer.domain.auth.dto.KakaoMemberAndExistDto;
import com.BeilsangServer.domain.auth.dto.KakaoResponseDto;
import com.BeilsangServer.domain.auth.dto.RefreshRequestDto;
import com.BeilsangServer.domain.auth.dto.RefreshResponseDto;
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
    @Transactional
    public  KakaoResponseDto loginWithKakao(String accessToken, HttpServletResponse response) {
        KakaoMemberAndExistDto kakaoMemberAndExistDto = kakaoAuthService.getUserProfileByToken(accessToken); //dto에 socialId,email,Provider 저장
        return getTokens(kakaoMemberAndExistDto.getMember().getSocialId(),kakaoMemberAndExistDto.getExistMember(), response);
    }



    @Transactional
    public void signup(MemberLoginDto memberLoginDto){

        Long memberId = SecurityUtil.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 멤버입니다."));
        member.setMemberInfo(memberLoginDto);

    }

    @Transactional
    public void kakaoRevoke(String accessToken){
        Long socialId = Long.valueOf(jwtTokenProvider.getPayload(accessToken));
        Member member = memberRepository.findBySocialId(socialId);
        memberRepository.delete(member);
    }




    //Access Token, Refresh Token 생성
    @Transactional
    public KakaoResponseDto getTokens(Long socialId, Boolean existMember, HttpServletResponse response) {
        final String accessToken = jwtTokenProvider.createAccessToken(socialId.toString());
        final String refreshToken = jwtTokenProvider.createRefreshToken();

        Member member = memberRepository.findBySocialId(socialId);
        member.setRefreshToken(refreshToken);

//        jwtTokenProvider.addRefreshTokenToCookie(refreshToken,response);

        return KakaoResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .existMember(existMember)
                .build();
    }

    // 리프레시 토큰으로 액세스토큰 새로 갱신
    public RefreshResponseDto refreshAccessToken(RefreshRequestDto refreshRequestDto) {

        String refreshToken = refreshRequestDto.getRefreshToken();
        //유효성 검사 실패 시
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Member member = memberRepository.findByRefreshToken(refreshToken);
        if(member == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String changeAccessToken = jwtTokenProvider.createAccessToken(member.getSocialId().toString());

        return RefreshResponseDto.builder()
                .accessToken(changeAccessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
