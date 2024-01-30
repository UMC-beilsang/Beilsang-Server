package com.BeilsangServer.domain.feed.dto;

import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.global.enums.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class FeedDTO {

    private Long id;

    private String review;

    private LocalDate uploadDate;

    private String feedUrl;

    private String challengeTitle;

    private Category category;

    @Builder
    private FeedDTO(Long id, String review, LocalDate uploadDate, String feedUrl, String challengeTitle, Category category){
        this.id = id;
        this.review = review;
        this.uploadDate = uploadDate;
        this.feedUrl = feedUrl;
        this.challengeTitle = challengeTitle;
        this.category = category;
    }


}