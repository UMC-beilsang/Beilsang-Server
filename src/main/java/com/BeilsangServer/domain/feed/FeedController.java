package com.BeilsangServer.domain.feed;

import com.BeilsangServer.domain.auth.util.SecurityUtil;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.service.FeedService;
import com.BeilsangServer.global.common.apiResponse.ApiResponse;
import com.BeilsangServer.global.common.apiResponse.ApiResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class FeedController {

    private final FeedService feedService;

    @PostMapping(value = "/feeds/{challengeId}",consumes = MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "피드 생성 API", description = "챌린지 인증을 통해 피드를 생성하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<Long> createFeed(
            @RequestPart MultipartFile file,
            @RequestPart String review,
            @PathVariable(name = "challengeId") Long challengeId
    ){
        //Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 1L;
        Long newFeedId = feedService.createFeed(file,review,challengeId,memberId);
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,newFeedId);
    }

    @GetMapping("/feeds/guide/{challengeId}")
    @Operation(summary = "인증 가이드 조회 API", description = "챌린지 인증 시 인증 가이드를 조회하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<ChallengeResponseDTO.ChallengeGuide> getGuide(
            @PathVariable(name = "challengeId") Long challengeId
    ){
        ChallengeResponseDTO.ChallengeGuide guide = feedService.getGuide(challengeId);
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,guide);
    }

    @GetMapping("/feeds/{feedId}")
    @Operation(summary = "특정 피드 정보 조회 API", description = "사용자가 선택한 피드의 정보를 조회하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<FeedDTO> getFeed(
            @PathVariable(name = "feedId") Long feedId
    ){
        //Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 1L;
        FeedDTO feedDTO = feedService.getFeed(feedId,memberId);
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,feedDTO);
    }

    @GetMapping("/search")
    @Operation(summary = "챌린지 제목으로 피드 검색 API", description = "검색어가 제목에 포함된 챌린지와 관련된 피드를 검색하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<FeedDTO.previewChallengeAndFeed> searchFeed(
            @RequestParam("name") String name
    ){
        FeedDTO.previewChallengeAndFeed previewChallengeAndFeed = feedService.searchChallengeAndFeed(name);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,previewChallengeAndFeed);
    }

    @PostMapping("/feeds/{feedId}/likes")
    @Operation(summary = "피드 좋아요 API", description = "사용자가 원하는 피드에 좋아요를 누르는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<Long> feedLike(
            @PathVariable(name = "feedId") Long feedId
    ){
        //Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 1L;

        Long feedLikeId = feedService.feedLike(feedId,memberId);
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,feedLikeId);
    }

    @DeleteMapping("/feeds/{feedId}/likes")
    @Operation(summary = "피드 좋아요 취소 API", description = "좋아요를 취소하는 삭제하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<Long> feedUnLike(
            @PathVariable(name = "feedId") Long feedId
    ){
        //Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 1L;
        Long feedUnLikeId = feedService.feedUnLike(feedId,memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,feedUnLikeId);
    }

    @GetMapping("/feeds/category/{category}")
    @Operation(summary = "카테고리로 필터링한 피드 조회 API", description = "선택된 카테고리에 해당하는 피드를 조회하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<FeedDTO.previewFeedListDto> getFeedByCategory(
            @PathVariable(name = "category") String category
    ){
        FeedDTO.previewFeedListDto feedDTOList = feedService.getFeedByCategory(category);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,feedDTOList);
    }

    @GetMapping("/feeds/{status}/{category}")
    @Operation(summary = "카테고리와 상태로 필터링한 내 피드 조회 API", description = "나의 피드에 대해 카테고리와 챌린지 상태로 필터링하여 피드를 조회하는 API 입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",description = "성공")
    })
    public ApiResponse<FeedDTO.previewFeedListDto> getFeedByStatusAndCategory(
            @PathVariable(name = "status") String status,
            @PathVariable(name = "category") String category
    ){
        //Long memberId = SecurityUtil.getCurrentUserId();
        Long memberId = 1L;

        FeedDTO.previewFeedListDto feedDTOList = feedService.getFeedByStatusAndCategory(status,category,memberId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,feedDTOList);
    }
}
