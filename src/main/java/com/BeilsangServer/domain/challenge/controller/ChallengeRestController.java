package com.BeilsangServer.domain.challenge.controller;

import com.BeilsangServer.domain.challenge.converter.ChallengeConverter;
import com.BeilsangServer.domain.challenge.dto.ChallengeRequestDTO;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.service.ChallengeService;
import com.BeilsangServer.global.common.ApiResponse;
import com.BeilsangServer.global.common.ApiResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges")
public class ChallengeRestController {

    private final ChallengeService challengeService;

    @GetMapping
    public ApiResponse<ChallengeResponseDTO.CreateResultDTO> createChallenge(@RequestBody ChallengeRequestDTO.CreateDTO request) {

        Challenge challenge = challengeService.createChallenge(request);

        // 컨버터를 사용해 response DTO로 변환하여 응답
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, ChallengeConverter.toCreateResultDTO(challenge));
    }
}
