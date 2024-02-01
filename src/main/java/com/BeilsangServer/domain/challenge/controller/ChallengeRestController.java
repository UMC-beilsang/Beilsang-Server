package com.BeilsangServer.domain.challenge.controller;

import com.BeilsangServer.domain.challenge.converter.ChallengeConverter;
import com.BeilsangServer.domain.challenge.dto.ChallengeRequestDTO;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.service.ChallengeService;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.global.common.ApiResponse;
import com.BeilsangServer.global.common.ApiResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges")
public class ChallengeRestController {

    private final ChallengeService challengeService;

    @PostMapping("")
    public ApiResponse<ChallengeResponseDTO.CreateResultDTO> createChallenge(@RequestBody ChallengeRequestDTO.CreateDTO request) {

        Challenge challenge = challengeService.createChallenge(request);

        // 컨버터를 사용해 response DTO로 변환하여 응답
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, ChallengeConverter.toCreateResultDTO(challenge));
    }

    @GetMapping("/{challengeId}")
    public ApiResponse<ChallengeResponseDTO.GetChallengeDTO> getChallenge(@PathVariable(name = "challengeId") Long challengeId) {

        Challenge challenge = challengeService.getChallenge(challengeId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, ChallengeConverter.toGetChallengeDTO(challenge));
    }
}
