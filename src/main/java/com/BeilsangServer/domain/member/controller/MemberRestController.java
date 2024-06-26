package com.BeilsangServer.domain.member.controller;

import com.BeilsangServer.domain.auth.util.SecurityUtil;
import com.BeilsangServer.domain.member.dto.MemberResponseDTO;
import com.BeilsangServer.domain.member.dto.MemberUpdateDto;
import com.BeilsangServer.domain.member.service.MemberService;
import com.BeilsangServer.domain.point.dto.PointResponseDTO;
import com.BeilsangServer.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberRestController {

    private final MemberService memberService;
    @GetMapping("/mypage")
    @Operation(summary = "마이 페이지 조회 API",
            description = "사용자의 마이 페이지 정보를 조회하는 API 입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<MemberResponseDTO.myPageDTO> getMyPage(){

        Long memberId = SecurityUtil.getCurrentUserId();

        MemberResponseDTO.myPageDTO response = memberService.getMyPage(memberId);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/mypage/points")
    @Operation(summary = "사용자의 포인트 로그 조회 API",
            description = "사용자의 포인트 지급/사용/소멸 내역과 전체 보유 포인트를 조회하는 API 입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<PointResponseDTO.pointLogListDTO> getPointLog(){

        Long memberId = SecurityUtil.getCurrentUserId();

        PointResponseDTO.pointLogListDTO response = memberService.getPointLog(memberId);

        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/profile")
    @Operation(summary = "사용자의 프로필 수정 API",
            description = "프로필 내용을 수정할 수 있는 API 입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<MemberResponseDTO.profileDTO> updateProfile(
            @RequestBody MemberUpdateDto memberUpdateDto
            ){

        Long memberId = SecurityUtil.getCurrentUserId();

        MemberResponseDTO.profileDTO response = memberService.updateProfile(memberUpdateDto,memberId);

        return ApiResponse.onSuccess(response);
    }

    @PatchMapping(value = "/profile/image",consumes = MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "사용자의 프로필 사진 수정 API",
            description = "프로필 사진을 수정할 수 있는 API 입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<MemberResponseDTO.ProfileImageDTO> updateProfileImage(
            @ModelAttribute MemberUpdateDto.ProfileImageDTO request
    ){
        Long memberId = SecurityUtil.getCurrentUserId();

        MemberResponseDTO.ProfileImageDTO response = memberService.updateProfileImage(request,memberId);

        return ApiResponse.onSuccess(response);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/join/check/nickname")
    public ApiResponse<Boolean> checkNickName(
        @RequestParam("name") String name
    ){

        boolean isExists = memberService.checkNickName(name);
        return ApiResponse.onSuccess(isExists);
    }

    @Operation(
            summary = "멤버의 닉네임 존재 여부 확인 API",
            description = "멤버의 닉네임이 존재하면 true, 존재하지 않으면 false를 보내는 API 입니다",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/join/check/existnickname")
    public ApiResponse<Boolean> checkNickNameExists(){
        Long memberId = SecurityUtil.getCurrentUserId();

        boolean isNickNameExists = memberService.checkNickNameExists(memberId);

        return ApiResponse.onSuccess(isNickNameExists);
    }

    @GetMapping("/check/{challengeId}")
    @Operation(
            summary = "멤버의 챌린지 참여 조회 API",
            description = "챌린지ID를 PathVariable로 입력 받아 해당하는 챌린지에 멤버가 참여 중인지와, 멤버가 참여하고 있는 챌린지의 ID들을 알아내는 API입니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @Parameter(name = "challengeId", description = "챌린지 ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<MemberResponseDTO.CheckEnrolledDTO> checkIsMemberEnrolled(@PathVariable(name = "challengeId") Long challengeId) {

        Long memberId = SecurityUtil.getCurrentUserId();

        MemberResponseDTO.CheckEnrolledDTO response = memberService.checkEnroll(memberId, challengeId);

        return ApiResponse.onSuccess(response);
    }
}
