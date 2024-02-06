package com.BeilsangServer.domain.challenge.converter;

import com.BeilsangServer.domain.challenge.dto.ChallengeRequestDTO;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.entity.ChallengeNote;
import com.BeilsangServer.domain.member.converter.MemberConverter;
import com.BeilsangServer.domain.member.entity.Member;

import java.time.LocalDate;
import java.util.List;

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

    public static ChallengeResponseDTO.ChallengeDTO toChallengeDTO(Challenge challenge, Integer dDay, List<ChallengeResponseDTO.RecommendChallengeDTO> recommendChallengeDTOList) {

        List<String> challengeNotes = toStringChallengeNotes(challenge.getChallengeNotes());

        return ChallengeResponseDTO.ChallengeDTO.builder()
                .attendeeCount(challenge.getAttendeeCount())
                .createdMember(null)
                .createdDate(challenge.getCreatedAt().toLocalDate())
                .imageUrl(challenge.getImageUrl())
                .certImageUrl(challenge.getCertImageUrl())
                .title(challenge.getTitle())
                .startDate(challenge.getStartDate())
                .category(challenge.getCategory())
                .details(challenge.getDetails())
                .challengeNotes(challengeNotes)
                .joinPoint(challenge.getJoinPoint())
                .dDay(dDay)
                .recommendChallengeDTOList(recommendChallengeDTOList)
                .build();

    }

    public static ChallengeResponseDTO.CreateResultDTO toGuideResultDto(Challenge challenge){
        return ChallengeResponseDTO.CreateResultDTO.builder()
                .certImageUrl(challenge.getCertImageUrl())
                .challengeNotes(challenge.getChallengeNotes())
                .build();
    }

    public static List<String> toStringChallengeNotes(List<ChallengeNote> challengeNotes) {

        return challengeNotes
                .stream()
                .map(ChallengeNote::getNote)
                .toList();
    }

    public static ChallengeResponseDTO.ChallengePreviewDTO toChallengePreviewDTO(Challenge challenge) {

        return ChallengeResponseDTO.ChallengePreviewDTO.builder()
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .imageUrl(challenge.getImageUrl())
                .createdMember(null)
                .attendeeCount(challenge.getAttendeeCount())
                .build();
    }

    /***
     * Challenge 리스트를 ChallengePreviewListDTO로 변환하기
     * @param challenges 챌린지 목록
     * @return ChallengePreviewListDTO
     */
    public static ChallengeResponseDTO.ChallengePreviewListDTO toChallengePreviewListDTO(List<Challenge> challenges) {

        List<ChallengeResponseDTO.ChallengePreviewDTO> challengePreviews = challenges.stream().map(challenge -> ChallengeResponseDTO.ChallengePreviewDTO.builder()
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .imageUrl(challenge.getImageUrl())
                .createdMember(null)
                .attendeeCount(challenge.getAttendeeCount())
                .build()
        ).toList();

        return ChallengeResponseDTO.ChallengePreviewListDTO.builder().challenges(challengePreviews).build();
    }

    /***
     * Challenge 리스트를 ChallengePreviewListDTO로 변환하기
     * @param member 참여하는 멤버
     * @param challenge 참여하는 챌린지
     * @return JoinChallengeDTO
     */
    public static ChallengeResponseDTO.JoinChallengeDTO toJoinChallengeDTO(Member member, Challenge challenge) {

        return ChallengeResponseDTO.JoinChallengeDTO.builder()
                .memberDTO(MemberConverter.toMemberDTO(member))
                .challengePreviewDTO(ChallengeConverter.toChallengePreviewDTO(challenge))
                .build();
    }
}
