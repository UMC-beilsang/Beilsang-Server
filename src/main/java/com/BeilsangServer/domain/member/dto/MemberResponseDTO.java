package com.BeilsangServer.domain.member.dto;

import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.global.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
        String nickName;
        String profileImage;
        String address;
        Gender gender;
        LocalDate birth;
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


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckEnrolledDTO {
        private Boolean isEnrolled;
        private List<Long> enrolledChallengeIds;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileImageDTO{
        private Long memberId;
        private String imgageUrl;
    }
}
