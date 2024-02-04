package com.BeilsangServer.domain.challenge.dto;
import com.BeilsangServer.domain.challenge.entity.ChallengeNote;
import com.BeilsangServer.global.enums.Category;
import com.BeilsangServer.global.enums.ChallengePeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChallengeResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateResultDTO {

        private Long id;
        private String title;
        private Category category;
        private LocalDate startDate;
        private LocalDate finishDate;
        private Integer joinPoint;
        private String imageUrl;
        private String certImageUrl;
        private String details;
        private List<ChallengeNote> challengeNotes = new ArrayList<>();
        private ChallengePeriod period;
        private Integer totalGoalDay;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeDTO {

        private Integer attendeeCount;
        private String createdMember;
        private LocalDate createdDate;
        private String imageUrl;
        private String certImageUrl;
        private String title;
        private LocalDate startDate;
        private Category category;
        private String details;
        private Integer joinPoint;
        private Integer dDay;
        // 챌린지 유의사항
        private List<String> challengeNotes;
        // 추천 챌린지 목록
        private List<RecommendChallengeDTO> recommendChallengeDTOList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendChallengeDTO {

        private Long challengeId;
        private String imageUrl;
        private String title;
        private Category category;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengePreviewDTO { // 챌린지 미리보기 DTO

        private Long categoryId;
        private String title;
        private String imageUrl;
        private String createdMember;
        private Integer attendeeCount;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengePreviewListDTO {

        private List<ChallengePreviewDTO> challenges;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeListWithCountDTO{
        private ChallengePreviewListDTO challenges;
        private Integer count;
    }
}
