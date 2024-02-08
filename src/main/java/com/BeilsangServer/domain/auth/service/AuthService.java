package com.BeilsangServer.domain.auth.service;


import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.BeilsangServer.global.jwt.JwtTokenProvider;
import com.BeilsangServer.global.jwt.exception.CustomException;
import com.BeilsangServer.global.jwt.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoAuthService kakaoAuthService;

    //카카오 로그인
    public  String loginWithKakao(String accessToken, HttpServletResponse response) {
        Member member = kakaoAuthService.getUserProfileByToken(accessToken);
        return getTokens(member.getSocialId(), response);
    }

    //Access Token, Refresh Token 생성
    public String getTokens(Long id, HttpServletResponse response) {
        final String accessToken = jwtTokenProvider.createAccessToken(id.toString());
        final String refreshToken = jwtTokenProvider.createRefreshToken();

        Member member = memberRepository.findBySocialId(id);
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);

        jwtTokenProvider.addRefreshTokenToCookie(refreshToken,response);
        return accessToken;

    }

    // 리프레시 토큰으로 액세스토큰 새로 갱신
    public String refreshAccessToken(String refreshToken) {
        Member member = memberRepository.findByRefreshToken(refreshToken);
        if(member == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        //유효성 검사 실패 시
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        return jwtTokenProvider.createAccessToken(member.getSocialId().toString());
    }
}
