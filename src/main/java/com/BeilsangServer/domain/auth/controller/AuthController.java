package com.BeilsangServer.domain.auth.controller;


import com.BeilsangServer.domain.auth.dto.AppleMemberDto;
import com.BeilsangServer.domain.auth.dto.KakaoMemberDto;
import com.BeilsangServer.domain.auth.service.AuthService;
import com.BeilsangServer.domain.auth.service.SecurityService;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.global.common.apiResponse.ApiResponse;
import com.BeilsangServer.global.common.apiResponse.ApiResponseStatus;
import com.BeilsangServer.global.jwt.JwtTokenProvider;
import com.BeilsangServer.global.jwt.dto.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
@Tag(name = "Auth", description = "로그인 관련된 API")
public class AuthController {
    private static final String KAKAO_ID = "4bcf6f023bec74eb18985f6e61a5637d";
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityService securityService;

    @PostMapping("/kakao/login")
    @Operation(summary = "Kakao Login")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<MemberResponseDto.TokenInfo> login(@RequestBody KakaoMemberDto kakaoMemberDto)throws Exception {

        Member kakaoInfo = authService.getKakaoInfo(kakaoMemberDto);
        MemberResponseDto.TokenInfo tokenDto = securityService.kakaologin(kakaoInfo);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,tokenDto);
    }

    @PostMapping("/apple/login")
    @Operation(summary = "apple Login")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<MemberResponseDto.TokenInfo> login(@RequestBody AppleMemberDto appleMemberDto)throws Exception {

        Member appleInfo = authService.getAppleInfo(appleMemberDto);
        MemberResponseDto.TokenInfo tokenDto = securityService.appleLogin(appleInfo);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,tokenDto);
    }




}