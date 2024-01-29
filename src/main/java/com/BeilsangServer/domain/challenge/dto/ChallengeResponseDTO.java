package com.BeilsangServer.domain.challenge.dto;

import com.BeilsangServer.domain.category.entity.Category;
import com.BeilsangServer.domain.challenge.entity.ChallengeNote;
import com.BeilsangServer.domain.challenge.entity.ChallengePeriod;
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
        private LocalDate startDate;
        private LocalDate finishDate;
        private Long joinPoint;
        private String imageUrl;
        private String certImageUrl;
        private String details;
        private List<ChallengeNote> challengeNotes = new ArrayList<>();
        private ChallengePeriod period;
        private Long totalGoalDay;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetChallengeDTO {

        private Integer attendeeCount;
        private String createdMember;
        private LocalDate createdDate;
        private String imageUrl;
        private String certImageUrl;
        private String title;
        private LocalDate startDate;
        private Category category;
        private String details;
        private List<String> challengeNotes;
        private Integer joinPoint;

        // 추천 챌린지 목록
        private List<RecommendChallengeDTO> recommendChallengeDTOList;
    }

    public static class RecommendChallengeDTO {

    }
}
