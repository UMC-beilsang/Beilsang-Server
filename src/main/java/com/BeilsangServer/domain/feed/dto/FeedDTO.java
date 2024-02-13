package com.BeilsangServer.domain.feed.dto;

import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.global.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private boolean like;
    private Long day;
    private String nickName;
    private String profileImage;

    @Builder
    private FeedDTO(Long id, String review, LocalDate uploadDate, String feedUrl, String challengeTitle, Category category, Long likes, boolean like, Long day, String nickName, String profileImage){
        this.id = id;
        this.review = review;
        this.uploadDate = uploadDate;
        this.feedUrl = feedUrl;
        this.challengeTitle = challengeTitle;
        this.category = category;
        this.likes = likes;
        this.like = like;
        this.day = day;
        this.nickName = nickName;
        this.profileImage = profileImage;
    }

    @Schema(description = "피드 미리보기 DTO")
    @Builder
    @Getter
    public static class previewFeedDto{

        @Schema(description = "feed 고유 아이디")
        private Long feedId;
        @Schema(description = "feed 이미지 url")
        private String feedUrl;
        @Schema(description = "현재 날짜 - 피드 업로드 날짜")
        private Long day;
    }

    @Schema(description = "피드 미리보기 DTO List")
    @Builder
    @Getter
    public static class previewFeedListDto{
        @Schema(description = "피드 미리보기 DTO")
        private List<previewFeedDto> feeds;
    }

    @Schema(description = "챌린지 이름으로 검색한 챌린지&피드 결과 DTO")
    @Builder
    @Getter
    public static class previewChallengeAndFeed{
        @Schema(description = "챌린지 미리보기 DTO List")
        private List<ChallengeResponseDTO.ChallengePreviewDTO> challenges;
        @Schema(description = "피드 미리보기 DTO List")
        private List<FeedDTO.previewFeedDto> feeds;
    }
}