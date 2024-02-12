package com.BeilsangServer.domain.auth.controller;


import com.BeilsangServer.domain.auth.dto.*;
import com.BeilsangServer.domain.auth.service.AuthService;
import com.BeilsangServer.domain.member.dto.MemberLoginDto;
import com.BeilsangServer.global.common.apiResponse.ApiResponse;
import com.BeilsangServer.global.common.apiResponse.ApiResponseStatus;
import com.BeilsangServer.global.jwt.exception.CustomException;
import com.BeilsangServer.global.jwt.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
@Tag(name = "Auth", description = "로그인 관련된 API")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/{provider}/login")
    @Operation(summary = "소셜 로그인 API")
        @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<KakaoResponseDto> login(@PathVariable String provider, @RequestBody KakaoRequestDto kakaoRequestDto,
                                                          HttpServletResponse response) {
        KakaoResponseDto kakaoResponseDto = new KakaoResponseDto();
        switch (provider) {
            case "KAKAO":
                kakaoResponseDto = authService.loginWithKakao(kakaoRequestDto.getAccesstoken(), response);
        }
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,kakaoResponseDto);
    }

    @PostMapping("/signup")
    @Operation(summary = "소셜 로그인 후 로그인 폼 받아서 자체 회원가입 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Object> signup(@RequestBody MemberLoginDto memberLoginDto)
    {
      authService.signup(memberLoginDto);

      return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS);
    }

    @DeleteMapping("/{provider}/revoke")
    @Operation(summary = "회원 탈퇴")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Object> revoke(@PathVariable String provider, @RequestBody String accessToken) {
        switch (provider) {
            case "KAKAO":
                // 구현예정
        }
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS);
    }

    // 리프레시 토큰으로 액세스토큰 재발급 받는 로직
    @PostMapping("/token/refresh")
    @Operation(summary = "Access Token 재발급 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<RefreshTokenResponseDto> tokenRefresh(HttpServletRequest request) {
        RefreshTokenResponseDto refreshTokenResponseDto = new RefreshTokenResponseDto();
        Cookie[] list = request.getCookies();
        if(list == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Cookie refreshTokenCookie = Arrays.stream(list).filter(cookie -> cookie.getName().equals("refresh_token")).collect(Collectors.toList()).get(0);

        if(refreshTokenCookie == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        String accessToken = authService.refreshAccessToken(refreshTokenCookie.getValue());
        refreshTokenResponseDto.setAccessToken(accessToken);
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,refreshTokenResponseDto);
    }

//    @PostMapping("/kakao/login")
//    @Operation(summary = "Kakao Login")
//    @io.swagger.v3.oas.annotations.responses.ApiResponses({
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
//    })
//    public ApiResponse<MemberResponseDto.TokenInfo> login(@RequestBody KakaoMemberDto kakaoMemberDto)throws Exception {
//
//        Member kakaoInfo = authService.getKakaoInfo(kakaoMemberDto);
//        MemberResponseDto.TokenInfo tokenDto = securityService.kakaologin(kakaoInfo);
//
//        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,tokenDto);
//    }
//
//    @PostMapping("/apple/login")
//    @Operation(summary = "apple Login")
//    @io.swagger.v3.oas.annotations.responses.ApiResponses({
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
//    })
//    public ApiResponse<MemberResponseDto.TokenInfo> login(@RequestBody AppleMemberDto appleMemberDto)throws Exception {
//
//        Member appleInfo = authService.getAppleInfo(appleMemberDto);
//        MemberResponseDto.TokenInfo tokenDto = securityService.appleLogin(appleInfo);
//
//        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,tokenDto);
//    }




}