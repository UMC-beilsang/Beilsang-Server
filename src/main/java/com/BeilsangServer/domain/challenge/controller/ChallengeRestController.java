package com.BeilsangServer.domain.challenge.controller;
import com.BeilsangServer.domain.auth.util.SecurityUtil;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges")
public class ChallengeRestController {

    private final ChallengeService challengeService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "챌린지 생성 API", description = "필요한 정보를 받아 챌린지를 생성하는 API입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ChallengeResponseDTO.ChallengePreviewDTO> createChallenge(@ModelAttribute ChallengeRequestDTO.CreateChallengeDTO request) {

        Long memberId = 1L;

        ChallengeResponseDTO.ChallengePreviewDTO response = challengeService.createChallenge(request, memberId);

        // 컨버터를 사용해 response DTO로 변환하여 응답
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, response);
    }

    @GetMapping("/{challengeId}")
    @Operation(summary = "챌린지 상세 조회 API", description = "챌린지ID를 PathVariable로 입력 받아 해당하는 챌린지의 상세 내용을 조회하는 API입니다.")
    @Parameter(name = "challengeId", description = "챌린지 ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<ChallengeResponseDTO.ChallengeDTO> getChallenge(@PathVariable(name = "challengeId") Long challengeId) {

        //Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 1L;

        ChallengeResponseDTO.ChallengeDTO response = challengeService.getChallenge(challengeId,memberId);

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
    @Operation(summary = "명예의 전당 조회 API", description = "카테고리별로 찜 수가 가장 많은 상의 10개의 챌린지를 조회하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")
    })
    public ApiResponse<ChallengeResponseDTO.ChallengePreviewListDTO> getFamousChallengeList(
            @PathVariable(name = "category") String category
    ) {
        ChallengeResponseDTO.ChallengePreviewListDTO challenges = challengeService.getFamousChallengeList(category);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, challenges);
    }

    @GetMapping("/likes/{category}")
    @Operation(summary = "사용자의 찜한 챌린지 목록 조회 API", description = "사용자가 찜한 챌린지의 정보를 조회하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")
    })
    public ApiResponse<ChallengeResponseDTO.ChallengePreviewListDTO> getLikesList(
            @PathVariable(name = "category") String category
    ) {
        //Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 1L;
        ChallengeResponseDTO.ChallengePreviewListDTO challenges = challengeService.getLikesList(memberId,category);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, challenges);
    }

    @GetMapping("/{status}/{category}")
    @Operation(summary = "카테고리와 상태로 필터링한 사용자가 참여중인 챌린지 조회 API", description = "나의 피드에 대해 카테고리와 챌린지 상태로 필터링하여 사용자가 참여중인 챌린지 목록을 조회하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")
    })
    public ApiResponse<ChallengeResponseDTO.ChallengeListWithCountDTO> getChallengeByStatusAndCategory(
            @PathVariable(name = "status") String status,
            @PathVariable(name = "category") String category
    ) {
        // Long memberId = SecurityUtil.getCurrentUserId();
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

    @Operation(summary = "추천 챌린지 조회 API", description = "아직 시작 안한 챌린지들 중 참여인원이 가장 많은 2개의 챌린지를 미리보기로 보여주는 API입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/recommends")
    public ApiResponse<ChallengeResponseDTO.RecommendChallengeListDTO> getRecommendChallenges() {

        List<ChallengeResponseDTO.RecommendChallengeDTO> recommendChallengeList = challengeService.getRecommendChallenges();
        ChallengeResponseDTO.RecommendChallengeListDTO response = ChallengeConverter.toRecommendChallengeListDTO(recommendChallengeList);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS, response);
    }

    @PostMapping("/{challengeId}/likes")
    public ApiResponse<Long> challengeLike(
            @PathVariable(name = "challengeId") Long challengeId
    ){
        //Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 1L;

        Long challengeLikeId = challengeService.challengeLike(challengeId,memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,challengeLikeId);
    }

    @DeleteMapping("/{challengeId}/likes")
    public ApiResponse<Long> challengeUnLike(
            @PathVariable(name = "challengeId") Long challengeId
    ){
        //Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 1L;
        Long challengeUnLikeId = challengeService.challengeUnLike(challengeId,memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,challengeUnLikeId);
    }
}
