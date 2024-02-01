package com.BeilsangServer.domain.challenge.converter;

import com.BeilsangServer.domain.challenge.dto.ChallengeRequestDTO;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;

import java.time.LocalDate;

public class ChallengeConverter {

    public static Challenge toChallenge(ChallengeRequestDTO.CreateDTO request) {

        // 시작일로부터 기간(7일/30일)만큼 지난 날짜를 챌린지 종료 날짜로 설정
        Integer period = request.getPeriod().getDays();
        LocalDate finishDate = request.getStartDate().plusDays(period);

        return Challenge.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .startDate(request.getStartDate())
                .finishDate(finishDate)
                .joinPoint(request.getJoinPoint())
                .imageUrl(request.getImageUrl())
                .certImageUrl(request.getCertImageUrl())
                .details(request.getDetails())
                .period(request.getPeriod())
                .totalGoalDay(request.getTotalGoalDay())
                .attendeeCount(0)
                .build();
    }

    public static ChallengeResponseDTO.CreateResultDTO toCreateResultDTO(Challenge challenge) {

        return ChallengeResponseDTO.CreateResultDTO.builder()
                .title(challenge.getTitle())
                .category(challenge.getCategory())
                .startDate(challenge.getStartDate())
                .finishDate(challenge.getFinishDate())
                .joinPoint(challenge.getJoinPoint())
                .details(challenge.getDetails())
                .challengeNotes(challenge.getChallengeNotes())
                .period(challenge.getPeriod())
                .totalGoalDay(challenge.getTotalGoalDay())
                .build();
    }


    public static ChallengeResponseDTO.GetChallengeDTO toGetChallengeDTO(Challenge challenge) {

        ChallengeResponseDTO.GetChallengeDTO response = ChallengeResponseDTO.GetChallengeDTO.builder()
                .build();

        return null;
    }

    public static ChallengeResponseDTO.CreateResultDTO toGuideResultDto(Challenge challenge){
        return ChallengeResponseDTO.CreateResultDTO.builder()
                .certImageUrl(challenge.getCertImageUrl())
                .challengeNotes(challenge.getChallengeNotes())
                .build();
    }
}
