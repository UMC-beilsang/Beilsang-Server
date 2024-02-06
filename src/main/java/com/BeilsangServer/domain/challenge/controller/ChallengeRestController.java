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
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges")
public class ChallengeRestController {

    private final ChallengeService challengeService;

    @PostMapping("")
    @Operation(summary = "챌린지 생성 API", description = "필요한 정보를 받아 챌린지를 생성하는 API입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ChallengeResponseDTO.CreateResultDTO> createChallenge(@RequestBody ChallengeRequestDTO.CreateDTO request) {

        Challenge challenge = challengeService.createChallenge(request);

        // 컨버터를 사용해 response DTO로 변환하여 응답
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, ChallengeConverter.toCreateResultDTO(challenge));
    }

    @GetMapping("/{challengeId}")
    @Operation(summary = "카테고리 상세 조회 API", description = "챌린지ID를 PathVariable로 입력 받아 해당하는 챌린지의 상세 내용을 조회하는 API입니다.")
    @Parameter(name = "challengeId", description = "챌린지 ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ChallengeResponseDTO.ChallengeDTO> getChallenge(@PathVariable(name = "challengeId") Long challengeId) {

        ChallengeResponseDTO.ChallengeDTO response = challengeService.getChallenge(challengeId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, response);
    }

    @GetMapping("/categories/{category}")
    @Operation(summary = "카테고리별 챌린지 조회 API", description = "해당하는 카테고리를 PathVariable로 입력 받아 해당하는 챌린지 목록을 조회하는 API입니다.")
    @Parameters({
            @Parameter(name = "category", description = """
                    카테고리 이름입니다. 영어와 한글 대소문자 상관없이 가능합니다.
                    [TUMBLER(다회용컵), REFILL_STATION(리필스테이션), MULTIPLE_CONTAINERS(다회용기), ECO_PRODUCT(친환경제품),
                    PLOGGING(플로깅), VEGAN(비건), PUBLIC_TRANSPORT(대중교통), BIKE(자전거), RECYCLE(재활용)]""")
    })
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ChallengeResponseDTO.ChallengePreviewListDTO> getChallengeByCategory(@PathVariable(name = "category") String category) {

        ChallengeResponseDTO.ChallengePreviewListDTO response = challengeService.getChallengePreviewList(category);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, response);
    }

    @GetMapping("")
    @Operation(summary = "챌린지 전체 조회 API", description = "전체 챌린지 목록을 조회하는 API입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ChallengeResponseDTO.ChallengePreviewListDTO> getTotalChallenge() {

        ChallengeResponseDTO.ChallengePreviewListDTO response = challengeService.getChallengePreviewList();

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, response);
    }

    @GetMapping("/famous/{category}")
    public ApiResponse<ChallengeResponseDTO.ChallengePreviewListDTO> getFamousChallengeList(
            @PathVariable(name = "category") String category
    ) {
        ChallengeResponseDTO.ChallengePreviewListDTO challenges = challengeService.getFamousChallengeList(category);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, challenges);
    }

    @GetMapping("/likes")
    public ApiResponse<ChallengeResponseDTO.ChallengePreviewListDTO> getLikesList(
            @PathVariable(name = "memberId") Long memberId
    ) {
        ChallengeResponseDTO.ChallengePreviewListDTO challenges = challengeService.getLikesList(memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, challenges);
    }

    @GetMapping("/{status}/{category}")
    public ApiResponse<ChallengeResponseDTO.ChallengeListWithCountDTO> getChallengeByStatusAndCategory(
            @PathVariable(name = "status") String status,
            @PathVariable(name = "category") String category
    ) {
        Long memberId = 1L;

        ChallengeResponseDTO.ChallengeListWithCountDTO challenges = challengeService.getChallengeByStatusAndCategory(status, category, memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, challenges);
    }

    @Operation(summary = "챌린지 참여 API", description = "참여하는 챌린지 ID를 받아 참여하는 API입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @Parameter(name = "challengeId", description = "챌린지 ID")
    @PostMapping("/{challengeId}")
    public ApiResponse<ChallengeResponseDTO.JoinChallengeDTO> joinChallenge(@PathVariable(name = "challengeId") Long challengeId) {

        Long memberId = 1L;

        ChallengeResponseDTO.JoinChallengeDTO response = challengeService.joinChallenge(challengeId, memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, response);
    }
}
