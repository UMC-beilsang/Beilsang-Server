package com.BeilsangServer.domain.feed.controller;

import com.BeilsangServer.domain.auth.util.SecurityUtil;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.feed.dto.AddFeedRequestDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.service.FeedService;
import com.BeilsangServer.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;

@Tag(name = "feeds", description = "피드 api")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class FeedRestController {

    private final FeedService feedService;

    @PostMapping(value = "/feeds/{challengeId}",consumes = MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "피드 생성 API",
            description = "challengeId 를 통해 챌린지 인증을 하여 피드를 생성하는 API 입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "피드 생성 성공")
    })
    @Parameters({
            @Parameter(name = "challengeId", description = "id에 해당하는 챌린지에 대한 피드를 생성합니다.")
    })
    public ApiResponse<Long> createFeed(
            @ModelAttribute AddFeedRequestDTO.CreateFeedDTO request,
            @PathVariable(name = "challengeId") Long challengeId
    ){
        Long memberId = SecurityUtil.getCurrentUserId();

        Long newFeedId = feedService.createFeed(request,challengeId,memberId);

        return ApiResponse.onSuccess(newFeedId);
    }

    @GetMapping("/feeds/guide/{challengeId}")
    @Operation(summary = "인증 가이드 조회 API",
            description = "챌린지 인증 시 인증 가이드를 조회하는 API 입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<ChallengeResponseDTO.ChallengeGuide> getGuide(
            @PathVariable(name = "challengeId") Long challengeId
    ){

        ChallengeResponseDTO.ChallengeGuide response = feedService.getGuide(challengeId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/feeds/{feedId}")
    @Operation(summary = "특정 피드 정보 조회 API",
            description = "사용자가 선택한 피드의 정보를 조회하는 API 입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<FeedDTO> getFeed(
            @PathVariable(name = "feedId") Long feedId
    ){
        Long memberId = SecurityUtil.getCurrentUserId();

        FeedDTO response = feedService.getFeed(feedId,memberId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/search")
    @Operation(summary = "챌린지 제목으로 피드 검색 API",
            description = "검색어가 제목에 포함된 챌린지와 관련된 피드를 검색하는 API 입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<FeedDTO.previewChallengeAndFeed> searchFeed(
            @RequestParam("name") String name
    ){

        FeedDTO.previewChallengeAndFeed response = feedService.searchChallengeAndFeed(name);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/feeds/{feedId}/likes")
    @Operation(summary = "피드 좋아요 API",
            description = "사용자가 원하는 피드에 좋아요를 누르는 API 입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<Long> feedLike(
            @PathVariable(name = "feedId") Long feedId
    ){
        Long memberId = SecurityUtil.getCurrentUserId();

        Long feedLikeId = feedService.feedLike(feedId, memberId);
        return ApiResponse.onSuccess(feedLikeId);
    }

    @DeleteMapping("/feeds/{feedId}/likes")
    @Operation(summary = "피드 좋아요 취소 API",
            description = "좋아요를 취소하는 삭제하는 API 입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<Long> feedUnLike(
            @PathVariable(name = "feedId") Long feedId
    ){
        Long memberId = SecurityUtil.getCurrentUserId();

        Long feedUnLikeId = feedService.feedUnLike(feedId,memberId);

        return ApiResponse.onSuccess(feedUnLikeId);
    }

    @GetMapping("/feeds/category/{category}")
    @Operation(summary = "카테고리로 필터링한 피드 조회 API",
            description = "선택된 카테고리에 해당하는 피드를 조회하는 API 입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<FeedDTO.previewFeedListDto> getFeedByCategory(
            @PathVariable(name = "category") String category,
            @RequestParam("page") Integer page
    ){

        FeedDTO.previewFeedListDto response = feedService.getFeedByCategory(category,page);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/feeds/{status}/{category}")
    @Operation(summary = "카테고리와 상태로 필터링한 내 피드 조회 API",
            description = "나의 피드에 대해 카테고리와 챌린지 상태로 필터링하여 피드를 조회하는 API 입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<FeedDTO.previewFeedListDto> getFeedByStatusAndCategory(
            @PathVariable(name = "status") String status,
            @PathVariable(name = "category") String category
    ){
        Long memberId = SecurityUtil.getCurrentUserId();

        FeedDTO.previewFeedListDto response = feedService.getFeedByStatusAndCategory(status,category,memberId);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/feeds/gallery/{challengeId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<FeedDTO.previewFeedListDto> getGallery(
            @PathVariable(name = "challengeId") Long challengeId
    ){
        FeedDTO.previewFeedListDto response = feedService.getGallery(challengeId);

        return ApiResponse.onSuccess(response);
    }
}
