package com.BeilsangServer.domain.member;

import com.BeilsangServer.domain.member.dto.MemberResponseDTO;
import com.BeilsangServer.domain.member.dto.MemberUpdateDto;
import com.BeilsangServer.domain.member.service.MemberService;
import com.BeilsangServer.domain.point.dto.PointResponseDTO;
import com.BeilsangServer.global.common.apiResponse.ApiResponse;
import com.BeilsangServer.global.common.apiResponse.ApiResponseStatus;
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
    public ApiResponse<MemberResponseDTO.myPageDTO> getMyPage(
            @PathVariable(name = "memberId") Long memberId
    ){

        MemberResponseDTO.myPageDTO myPageDTO = memberService.getMyPage(memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,myPageDTO);
    }

    @GetMapping("/mypage/points/{memberId}")
    public ApiResponse<PointResponseDTO.pointLogListDTO> getPointLog(
            @PathVariable(name = "memberId") Long memberId
    ){

        PointResponseDTO.pointLogListDTO pointLogListDTO = memberService.getPointLog(memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,pointLogListDTO);
    }

    @PostMapping("/profile/{memberId}")
    public ApiResponse<MemberResponseDTO.profileDTO> updateProfile(
            @PathVariable(name = "memberId") Long memberId,
            @RequestBody MemberUpdateDto memberUpdateDto
            ){
        MemberResponseDTO.profileDTO updated = memberService.updateProfile(memberUpdateDto,memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,updated);
    }

}
