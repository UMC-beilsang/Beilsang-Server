package com.BeilsangServer.domain.challenge.dto;

import com.BeilsangServer.global.enums.Category;
import com.BeilsangServer.global.enums.ChallengePeriod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public class ChallengeRequestDTO {

    @Builder
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateChallengeDTO {

        private MultipartFile mainImage; // 대표 사진
        private MultipartFile certImage; // 인증 사진
        private String title; // 챌린지 제목
        private LocalDate startDate; // 챌린지 시작 날짜
        private ChallengePeriod period; // 챌린지 기간(일주일/한달)
        private Integer totalGoalDay; // 목표 실천 일수
        private Category category; // 카테고리
        private String details; // 챌린지 세부설명
        private List<String> notes; // 챌린지 유의사항
        private Integer joinPoint; // 참여 포인트
    }
}
