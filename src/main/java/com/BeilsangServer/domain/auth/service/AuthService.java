package com.BeilsangServer.domain.auth.service;


import com.BeilsangServer.domain.auth.apple.dto.AppleMemberAndExistDto;
import com.BeilsangServer.domain.auth.apple.dto.AppleResponseDto;
import com.BeilsangServer.domain.auth.apple.service.AppleAuthService;
import com.BeilsangServer.domain.auth.dto.*;
import com.BeilsangServer.domain.member.dto.MemberLoginDto;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.BeilsangServer.domain.point.entity.PointLog;
import com.BeilsangServer.domain.point.repository.PointLogRepository;
import com.BeilsangServer.global.common.apiPayload.code.status.ErrorStatus;
import com.BeilsangServer.global.common.exception.handler.ErrorHandler;
import com.BeilsangServer.global.enums.PointName;
import com.BeilsangServer.global.enums.PointStatus;
import com.BeilsangServer.global.jwt.JwtTokenProvider;
import com.BeilsangServer.global.jwt.exception.CustomException;
import com.BeilsangServer.global.jwt.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@RequiredArgsConstructor
@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoAuthService kakaoAuthService;
    private final AppleAuthService appleAuthService;
    private final PointLogRepository pointLogRepository;

    //카카오 로그인
    @Transactional
    public  KakaoResponseDto loginWithKakao(String accessToken,String deviceToken, HttpServletResponse response) {
        KakaoMemberAndExistDto kakaoMemberAndExistDto = kakaoAuthService.getUserProfileByToken(accessToken); //dto에 socialId,email,Provider 저장
        kakaoMemberAndExistDto.getMember().updateDeviceToken(deviceToken);
        return getKaKaoTokens(kakaoMemberAndExistDto.getMember().getSocialId(),kakaoMemberAndExistDto.getExistMember(), response);
    }

    @Transactional
    public AppleResponseDto loginWithApple(String idToken, String deviceToken, HttpServletResponse response) {
        AppleMemberAndExistDto appleMemberAndExistDto =
                appleAuthService.getAppleMemberInfo(idToken);
        appleMemberAndExistDto.getMember().updateDeviceToken(deviceToken);
        return getAppleTokens(appleMemberAndExistDto.getMember().getSocialId(),appleMemberAndExistDto.getExistMember(),response);
    }



    @Transactional
    public void signup(MemberLoginDto memberLoginDto){
        String accessToken = memberLoginDto.getAccessToken();

        if(!jwtTokenProvider.validateToken(accessToken)||!StringUtils.hasText(accessToken)){
            throw new ErrorHandler(ErrorStatus.INVALID_ACCESS_TOKEN);
        }

        Long socialId = Long.valueOf(jwtTokenProvider.getPayload(accessToken));

        Member member = memberRepository.findBySocialId(socialId);

        member.setMemberInfo(memberLoginDto);

        // 회원가입 시 1000포인트 지급
        int welcomePoint = 1000;

        // 추천인(recommendNickname) null인지 확인 후 MemberRepository에서 일치하는 회원 있는지 확인
        // 그 후 추천인 포인트 추가
        String recommendNickname = memberLoginDto.getRecommendNickname();
        int recommendPoint = memberRepository.existsByNickName(recommendNickname) ? 500 : 0;

        int point = welcomePoint + recommendPoint;
        member.addPoint(point);

        // 포인트 기록 생성 및 디비 저장
        pointLogRepository.save(PointLog.builder()
                .pointName(PointName.NEW_MEMBER)
                .status(PointStatus.EARN)
                .value(point)
                .member(member)
                .build());
    }

    @Transactional
    public void kakaoRevoke(String accessToken){
        Long socialId = Long.valueOf(jwtTokenProvider.getPayload(accessToken));
        Member member = memberRepository.findBySocialId(socialId);
        memberRepository.delete(member);
    }

    @Transactional
    public void appleRevoke(String accessToken){
        Long socialId = Long.valueOf(jwtTokenProvider.getPayload(accessToken));
        Member member = memberRepository.findBySocialId(socialId);
        memberRepository.delete(member);
    }




    //Access Token, Refresh Token 생성
    @Transactional
    public KakaoResponseDto getKaKaoTokens(Long socialId, Boolean existMember, HttpServletResponse response) {
        final String accessToken = jwtTokenProvider.createAccessToken(socialId.toString());
        final String refreshToken = jwtTokenProvider.createRefreshToken();

        Member member = memberRepository.findBySocialId(socialId);
        member.setRefreshToken(refreshToken);


        return KakaoResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .existMember(existMember)
                .build();
    }


    //Access Token, Refresh Token 생성
    @Transactional
    public AppleResponseDto getAppleTokens(Long socialId, Boolean existMember, HttpServletResponse response) {
        final String accessToken = jwtTokenProvider.createAccessToken(socialId.toString());
        final String refreshToken = jwtTokenProvider.createRefreshToken();

        Member member = memberRepository.findBySocialId(socialId);
        member.setRefreshToken(refreshToken);

        return AppleResponseDto.builder()
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
            throw new ErrorHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        Member member = memberRepository.findByRefreshToken(refreshToken);
        if(member == null) {
            throw new ErrorHandler(ErrorStatus.NOT_EXIST_USER);
        }

        String changeAccessToken = jwtTokenProvider.createAccessToken(member.getSocialId().toString());

        return RefreshResponseDto.builder()
                .accessToken(changeAccessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
