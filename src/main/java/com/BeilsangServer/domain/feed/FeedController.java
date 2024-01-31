package com.BeilsangServer.domain.feed;

import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.feed.dto.AddFeedRequestDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.service.FeedService;
import com.BeilsangServer.global.common.ApiResponse;
import com.BeilsangServer.global.common.ApiResponseStatus;
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
    public ApiResponse<FeedDTO> createFeed(
            @RequestPart MultipartFile file,
            @RequestPart String review,
            @PathVariable(name = "challengeId") Long challengeId
    ){
        FeedDTO newFeed = feedService.createFeed(file,review,challengeId);
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,newFeed);
    }

    @GetMapping("/feeds/guide/{challengeId}")
    public ApiResponse<ChallengeResponseDTO.CreateResultDTO> getGuide(
            @PathVariable(name = "challengeId") Long challengeId
    ){
        ChallengeResponseDTO.CreateResultDTO guide = feedService.getGuide(challengeId);
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,guide);
    }

    @GetMapping("/feeds/{feedId}")
    public ApiResponse<FeedDTO> getFeed(
            @PathVariable(name = "feedId") Long feedId
    ){
        FeedDTO feedDTO = feedService.getFeed(feedId);
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,feedDTO);
    }

//    @GetMapping("/feeds/search")
//    public ApiResponse<List<FeedDTO>> searchFeed(
//            @RequestParam("name") String name
//    ){
//        List<FeedDTO> feedDTOList = feedService.searchFeed(name);
//
//        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,feedDTOList);
//    }

    @PostMapping("/feeds/{feedId}/likes")
    public ApiResponse<Long> feedLike(
            @PathVariable(name = "feedId") Long feedId
    ){
        Long feedLikeId = feedService.feedLike(feedId);
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,feedLikeId);
    }

    @DeleteMapping("/feeds/{feedId}/likes")
    public ApiResponse<Long> feedUnLike(
            @PathVariable(name = "feedId") Long feedId
    ){
        Long feedUnLikeId = feedService.feedUnLike(feedId);

        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,feedUnLikeId);
    }
}
