package com.BeilsangServer.domain.challenge.service;

import com.BeilsangServer.domain.challenge.dto.ChallengeRequestDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    public Challenge createChallenge(ChallengeRequestDTO.CreateChallengeDTO request) {

        // 시작일로부터 기간(7일/30일)만큼 지난 날짜를 챌린지 종료 날짜로 설정
        Integer period = request.getPeriod().getDays();
        LocalDate finishDate = request.getStartDate().plusDays(period);

        // 유의사항 매핑 필요
        Challenge challenge = Challenge.builder()
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .finishDate(finishDate)
                .joinPoint(request.getJoinPoint())
                .imageUrl(request.getImageUrl())
                .certImageUrl(request.getCertImageUrl())
                .details(request.getDetails())
                .build();

        return null;
    }
}
