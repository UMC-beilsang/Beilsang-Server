package com.BeilsangServer.domain.member;

import com.BeilsangServer.domain.member.dto.MemberResponseDTO;
import com.BeilsangServer.domain.member.dto.MemberUpdateDto;
import com.BeilsangServer.domain.member.service.MemberService;
import com.BeilsangServer.domain.point.dto.PointResponseDTO;
import com.BeilsangServer.global.common.apiResponse.ApiResponse;
import com.BeilsangServer.global.common.apiResponse.ApiResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;
    @GetMapping("/mypage/{memberId}")
    @Operation(summary = "마이 페이지 조회 API", description = "사용자의 마이 페이지 정보를 조회하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<MemberResponseDTO.myPageDTO> getMyPage(
            @PathVariable(name = "memberId") Long memberId
    ){

        MemberResponseDTO.myPageDTO myPageDTO = memberService.getMyPage(memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,myPageDTO);
    }

    @GetMapping("/mypage/points/{memberId}")
    @Operation(summary = "사용자의 포인트 로그 조회 API", description = "사용자의 포인트 지급/사용/소멸 내역과 전체 보유 포인트를 조회하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<PointResponseDTO.pointLogListDTO> getPointLog(
            @PathVariable(name = "memberId") Long memberId
    ){

        PointResponseDTO.pointLogListDTO pointLogListDTO = memberService.getPointLog(memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,pointLogListDTO);
    }

    @PostMapping("/profile/{memberId}")
    @Operation(summary = "사용자의 프로필 수정 API", description = "프로필 내용을 수정할 수 있는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<MemberResponseDTO.profileDTO> updateProfile(
            @PathVariable(name = "memberId") Long memberId,
            @RequestBody MemberUpdateDto memberUpdateDto
            ){
        MemberResponseDTO.profileDTO updated = memberService.updateProfile(memberUpdateDto,memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,updated);
    }

}
