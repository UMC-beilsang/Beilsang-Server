package com.BeilsangServer.domain.feed.dto;

import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.global.enums.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class FeedDTO {

    private Long id;

    private String review;

    private LocalDate uploadDate;

    private String feedUrl;

    private String challengeTitle;

    private Category category;

    private Long likes;

    @Builder
    private FeedDTO(Long id, String review, LocalDate uploadDate, String feedUrl, String challengeTitle, Category category, Long likes){
        this.id = id;
        this.review = review;
        this.uploadDate = uploadDate;
        this.feedUrl = feedUrl;
        this.challengeTitle = challengeTitle;
        this.category = category;
        this.likes = likes;
    }

    @Builder
    @Getter
    public static class previewFeedDto{
        private Long feedId;
        private String feedUrl;
    }

    @Builder
    @Getter
    public static class previewFeedListDto{
        private List<previewFeedDto> feeds;
    }

    @Builder
    @Getter
    public static class previewChallengeAndFeed{
        private List<ChallengeResponseDTO.ChallengePreviewDTO> challenges;
        private List<FeedDTO.previewFeedDto> feeds;

    }
}