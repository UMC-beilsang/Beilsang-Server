package com.BeilsangServer.domain.member.controller;

import com.BeilsangServer.domain.member.dto.MemberResponseDTO;
import com.BeilsangServer.domain.member.dto.MemberUpdateDto;
import com.BeilsangServer.domain.member.service.MemberService;
import com.BeilsangServer.domain.point.dto.PointResponseDTO;
import com.BeilsangServer.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberRestController {

    private final MemberService memberService;
    @GetMapping("/mypage/{memberId}")
    @Operation(summary = "마이 페이지 조회 API", description = "사용자의 마이 페이지 정보를 조회하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<MemberResponseDTO.myPageDTO> getMyPage(
            @PathVariable(name = "memberId") Long memberId
    ){

        MemberResponseDTO.myPageDTO response = memberService.getMyPage(memberId);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/mypage/points/{memberId}")
    @Operation(summary = "사용자의 포인트 로그 조회 API", description = "사용자의 포인트 지급/사용/소멸 내역과 전체 보유 포인트를 조회하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<PointResponseDTO.pointLogListDTO> getPointLog(
            @PathVariable(name = "memberId") Long memberId
    ){

        PointResponseDTO.pointLogListDTO response = memberService.getPointLog(memberId);

        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/profile")
    @Operation(summary = "사용자의 프로필 수정 API", description = "프로필 내용을 수정할 수 있는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<MemberResponseDTO.profileDTO> updateProfile(
            @RequestBody MemberUpdateDto memberUpdateDto
            ){
        //Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 1L;

        MemberResponseDTO.profileDTO response = memberService.updateProfile(memberUpdateDto,memberId);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/join/check/nickname")
    public ApiResponse<Boolean> checkNickName(
        @RequestParam("name") String name
    ){

        boolean isExists = memberService.checkNickName(name);
        return ApiResponse.onSuccess(isExists);
    }

    @GetMapping("/check/{challengeId}")
    @Operation(
            summary = "멤버의 챌린지 참여 조회 API",
            description = "챌린지ID를 PathVariable로 입력 받아 해당하는 챌린지에 멤버가 참여 중인지와, 멤버가 참여하고 있는 챌린지의 ID들을 알아내는 API입니다."
    )
    @Parameter(name = "challengeId", description = "챌린지 ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<MemberResponseDTO.CheckEnrolledDTO> checkIsMemberEnrolled(@PathVariable(name = "challengeId") Long challengeId) {

        //Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 1L;

        MemberResponseDTO.CheckEnrolledDTO response = memberService.checkEnroll(memberId, challengeId);

        return ApiResponse.onSuccess(response);
    }
}
