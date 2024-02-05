package com.BeilsangServer.domain.member.dto;

import com.BeilsangServer.domain.feed.dto.FeedDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String platform;
    private String refreshToken;

    @Builder
    @Getter
    public static class myPageDTO{
        Long feedNum;
        Integer achieve;
        Integer fail;
        String resolution;
        Long challenges;
        Long likes;
        Integer points;
        FeedDTO.previewFeedListDto feedDTOs;
    }
}