package com.BeilsangServer.domain.challenge.controller;

import com.BeilsangServer.domain.challenge.converter.ChallengeConverter;
import com.BeilsangServer.domain.challenge.dto.ChallengeRequestDTO;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.service.ChallengeService;
import com.BeilsangServer.global.common.apiResponse.ApiResponse;
import com.BeilsangServer.global.common.apiResponse.ApiResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

        ChallengeResponseDTO.GetChallengeDTO response = challengeService.getChallenge(challengeId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, response);
    }

    @GetMapping("/{category}")
    @Operation(summary = "카테고리별 챌린지 조회 API", description = "해당하는 카테고리를 PathVariable로 입력 받아 해당하는 챌린지 목록을 조회하는 API입니다.")
    @Parameter(name = "category", description = """
            카테고리 이름입니다. 영어와 한글 대소문자 상관없이 가능합니다.
            [TUMBLER(다회용컵), REFILL_STATION(리필스테이션), MULTIPLE_CONTAINERS(다회용기), ECO_PRODUCT(친환경제품),
            PLOGGING(플로깅), VEGAN(비건), PUBLIC_TRANSPORT(대중교통), BIKE(자전거), RECYCLE(재활용)]""")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ChallengeResponseDTO.ChallengeCategoryDTO> getChallengeByCategory(@PathVariable(name = "category") String category) {

        ChallengeResponseDTO.ChallengeCategoryDTO response = challengeService.getChallengeByCategory(category);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, response);
    }


}
