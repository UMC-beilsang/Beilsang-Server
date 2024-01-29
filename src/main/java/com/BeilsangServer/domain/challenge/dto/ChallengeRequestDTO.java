package com.BeilsangServer.domain.challenge.dto;

import com.BeilsangServer.global.enums.Category;
import com.BeilsangServer.global.enums.ChallengePeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class ChallengeRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {

        private String imageUrl; // 대표 사진 URL
        private String certImageUrl; // 모범 인증 사진 URL
        private String title; // 챌린지 제목
        private LocalDate startDate; // 챌린지 시작 날짜
        private ChallengePeriod period; // 챌린지 기간(일주일/한달)
        private Long totalGoalDay; // 목표 실천 일수
        private Category category; // 카테고리
        private String details; // 챌린지 세부설명
        private List<String> notes; // 챌린지 유의사항
        private Long joinPoint; // 참여 포인트
    }
}
