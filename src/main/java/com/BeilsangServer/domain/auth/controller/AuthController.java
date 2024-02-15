package com.BeilsangServer.domain.auth.controller;


import com.BeilsangServer.domain.auth.apple.dto.AppleLoginRequestDto;
import com.BeilsangServer.domain.auth.apple.dto.AppleResponseDto;
import com.BeilsangServer.domain.auth.apple.dto.AppleRevokeRequestDto;
import com.BeilsangServer.domain.auth.dto.*;
import com.BeilsangServer.domain.auth.service.AuthService;
import com.BeilsangServer.domain.member.dto.MemberLoginDto;
import com.BeilsangServer.global.common.apiPayload.ApiResponse;
import com.BeilsangServer.global.common.apiPayload.ApiResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
@Tag(name = "Auth", description = "로그인 관련된 API")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/kakao/login")
    @Operation(summary = "카카오 로그인 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<KakaoResponseDto> login(@RequestBody KakaoRequestDto kakaoRequestDto,
                                               HttpServletResponse response) {


        KakaoResponseDto kakaoResponseDto = authService.loginWithKakao(kakaoRequestDto.getAccesstoken(), response);

        return ApiResponse.onSuccess(kakaoResponseDto);
    }

    @PostMapping("/apple/login")
    @Operation(summary = "애플 로그인 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<AppleResponseDto> login(@RequestBody @Valid AppleLoginRequestDto appleLoginRequestDto, HttpServletResponse response) {

        AppleResponseDto appleResponseDto = authService.loginWithApple(appleLoginRequestDto.getIdToken(), response);
        return ApiResponse.onSuccess(appleResponseDto);
    }

    @PostMapping("/signup")
    @Operation(summary = "소셜 로그인 후 로그인 폼 받아서 자체 회원가입 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Object> signup(@RequestBody MemberLoginDto memberLoginDto) {
        authService.signup(memberLoginDto);

        return ApiResponse.onSuccess();
    }

    @DeleteMapping("/kakao/revoke")
    @Operation(summary = "카카오 회원 탈퇴 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Object> revoke(@RequestBody KakaoRevokeRequestDto kakaoRevokeRequestDto, HttpServletResponse response) {

        authService.kakaoRevoke(kakaoRevokeRequestDto.getAccesstoken());

        return ApiResponse.onSuccess();
    }

    @DeleteMapping("/apple/revoke")
    @Operation(summary = "애플 회원 탈퇴 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Object> revoke(@RequestBody AppleRevokeRequestDto appleRevokeRequestDto, HttpServletResponse response) {

        authService.appleRevoke(appleRevokeRequestDto.getAccessToken());

        return ApiResponse.onSuccess();
    }

    // 리프레시 토큰으로 액세스토큰 재발급 받는 로직
    @PostMapping("/token/refresh")
    @Operation(summary = "Access Token 재발급 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<RefreshResponseDto> tokenRefresh(@RequestBody RefreshRequestDto refreshRequestDto, HttpServletResponse httpServletResponse) {

        RefreshResponseDto refreshResponseDto = authService.refreshAccessToken(refreshRequestDto);

        return ApiResponse.onSuccess(refreshResponseDto);

    }
}


