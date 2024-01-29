package com.BeilsangServer.domain.feed;

import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.feed.dto.AddFeedRequestDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.service.FeedService;
import com.BeilsangServer.global.common.ApiResponse;
import com.BeilsangServer.global.common.ApiResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class FeedController {

    private final FeedService feedService;

    @PostMapping("/feeds/{challengeId}")
    public ApiResponse<FeedDTO> createFeed(
            @RequestBody AddFeedRequestDTO requestDTO,
            @PathVariable(name = "challengeId") Long challengeId
    ){
        FeedDTO newFeed = feedService.createFeed(requestDTO,challengeId);
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,newFeed);
    }

    @GetMapping("/feeds/guide/{challengeId}")
    public ApiResponse<ChallengeResponseDTO.CreateResultDTO> getGuide(
            @PathVariable(name = "challengeId") Long challengeId
    ){
        ChallengeResponseDTO.CreateResultDTO guide = feedService.getGuide(challengeId);
        return new ApiResponse<>(ApiResponseStatus.REQUEST_SUCCESS,guide);
    }
}
