package com.BeilsangServer.domain.challenge.controller;

import com.BeilsangServer.domain.challenge.converter.ChallengeConverter;
import com.BeilsangServer.domain.challenge.dto.ChallengeRequestDTO;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.service.ChallengeService;
import com.BeilsangServer.global.common.apiResponse.ApiResponse;
import com.BeilsangServer.global.common.apiResponse.ApiResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        ChallengeResponseDTO.GetChallengeDTO response = challengeService.getChallenge(challengeId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, response);
    }

    @GetMapping("/{category}")
    public ApiResponse<List<ChallengeResponseDTO.GetChallengeByCategoryDTO>> getChallengeByCategory(@PathVariable(name = "category") String category) {

        List<ChallengeResponseDTO.GetChallengeByCategoryDTO> response = challengeService.getChallengeByCategory(category);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, response);
    }


}
