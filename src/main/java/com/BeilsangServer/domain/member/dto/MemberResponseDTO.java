package com.BeilsangServer.domain.member.dto;

import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.member.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class MemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDTO {

        private Long memberId;
        private String nickName;
        private Integer totalPoint;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class myPageDTO{
        Long feedNum;
        Integer achieve;
        Integer fail;
        String resolution;
        Integer challenges;
        Long likes;
        Integer points;
        FeedDTO.previewFeedListDto feedDTOs;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class profileDTO{
        private String nickName;
        private LocalDate birth;
        private Gender gender;
        private String address;
    }
}
