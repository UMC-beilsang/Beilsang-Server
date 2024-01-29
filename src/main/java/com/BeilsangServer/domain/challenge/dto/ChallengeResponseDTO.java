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

        private Long joinPoint;

        private String imageUrl;

        private String certImageUrl;

        private String details;

        private List<ChallengeNote> challengeNotes = new ArrayList<>();

        private ChallengePeriod period;

        private Long totalGoalDay;
    }
}
