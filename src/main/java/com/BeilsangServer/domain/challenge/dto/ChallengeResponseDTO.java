package com.BeilsangServer.domain.challenge.dto;
import com.BeilsangServer.domain.challenge.entity.ChallengeNote;
import com.BeilsangServer.domain.member.dto.MemberResponseDTO;
import com.BeilsangServer.global.enums.Category;
import com.BeilsangServer.global.enums.ChallengePeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChallengeResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeDTO {

        private Integer attendeeCount;
        private String hostName;
        private LocalDate createdDate;
        private String imageUrl;
        private String certImageUrl;
        private String title;
        private LocalDate startDate;
        private DayOfWeek dayOfWeek;
        private Category category;
        private String details;
        private Integer joinPoint;
        private Integer dDay; // 챌린지 유의사항
        private List<String> challengeNotes; // 찜 개수
        private Integer likes; // 찜 여부
        private boolean like;
        private ChallengePeriod period;
        private Integer totalGoalDay;
        private Float achieveRate;
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
        private Integer attendeeCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendChallengeListDTO {

        private List<RecommendChallengeDTO> recommendChallengeDTOList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengePreviewDTO { // 챌린지 미리보기 DTO

        private Long challengeId;
        private String title;
        private String imageUrl;
        private String hostName;
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
    public static class MyChallengePreviewDTO { // 챌린지 미리보기 DTO

        private Long challengeId;
        private String title;
        private String imageUrl;
        private Float achieveRate;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyChallengePreviewListDTO {

        private List<MyChallengePreviewDTO> challenges;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeListWithCountDTO{
        private ChallengePreviewListDTO challenges;
        private Integer count;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeGuide{
        private String certImage;
        private List<String> challengeNoteList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinChallengeDTO {
        private ChallengePreviewDTO challengePreviewDTO;
        private MemberResponseDTO.MemberDTO memberDTO;
    }
}
