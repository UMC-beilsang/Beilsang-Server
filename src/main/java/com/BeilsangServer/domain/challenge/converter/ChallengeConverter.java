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

    public static Challenge toChallenge(ChallengeRequestDTO.CreateDTO request, String mainImageUrl, String certImageUrl) {

        // 시작일로부터 기간(7일/30일)만큼 지난 날짜를 챌린지 종료 날짜로 설정
        Integer period = request.getPeriod().getDays();
        LocalDate finishDate = request.getStartDate().plusDays(period);

        return Challenge.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .startDate(request.getStartDate())
                .finishDate(finishDate)
                .joinPoint(request.getJoinPoint())
                .mainImageUrl(mainImageUrl)
                .certImageUrl(certImageUrl)
                .details(request.getDetails())
                .period(request.getPeriod())
                .totalGoalDay(request.getTotalGoalDay())
                .attendeeCount(1)
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

    public static ChallengeResponseDTO.ChallengeDTO toChallengeDTO(Challenge challenge, Integer dDay, String hostName) {

        List<String> challengeNotes = toStringChallengeNotes(challenge.getChallengeNotes());

        return ChallengeResponseDTO.ChallengeDTO.builder()
                .attendeeCount(challenge.getAttendeeCount())
                .hostName(hostName)
                .createdDate(challenge.getCreatedAt().toLocalDate())
                .imageUrl(challenge.getMainImageUrl())
                .certImageUrl(challenge.getCertImageUrl())
                .title(challenge.getTitle())
                .startDate(challenge.getStartDate())
                .category(challenge.getCategory())
                .details(challenge.getDetails())
                .challengeNotes(challengeNotes)
                .joinPoint(challenge.getJoinPoint())
                .dDay(dDay)
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

    public static ChallengeResponseDTO.ChallengePreviewDTO toChallengePreviewDTO(Challenge challenge, String hostName) {

        return ChallengeResponseDTO.ChallengePreviewDTO.builder()
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .imageUrl(challenge.getMainImageUrl())
                .hostName(hostName)
                .attendeeCount(challenge.getAttendeeCount())
                .build();
    }

    /***
     * Challenge 리스트를 ChallengePreviewListDTO로 변환하기
     * @param challenges 챌린지 목록
     * @return ChallengePreviewListDTO
     */
    public static ChallengeResponseDTO.ChallengePreviewListDTO toChallengePreviewListDTO(List<Challenge> challenges) {

        List<ChallengeResponseDTO.ChallengePreviewDTO> challengePreviews = challenges.stream()
                .map(challenge -> ChallengeResponseDTO.ChallengePreviewDTO.builder()
                                .challengeId(challenge.getId())
                                .title(challenge.getTitle())
                                .imageUrl(challenge.getMainImageUrl())
                                .hostName(null)
                                .attendeeCount(challenge.getAttendeeCount())
                                .build())
                .toList();

        return ChallengeResponseDTO.ChallengePreviewListDTO.builder().challenges(challengePreviews).build();
    }

    /***
     * Challenge 리스트를 ChallengePreviewListDTO로 변환하기
     * @param member 참여하는 멤버
     * @param challenge 참여하는 챌린지
     * @param hostName 챌린지 호스트 이름
     * @return JoinChallengeDTO
     */
    public static ChallengeResponseDTO.JoinChallengeDTO toJoinChallengeDTO(Member member, Challenge challenge, String hostName) {

        return ChallengeResponseDTO.JoinChallengeDTO.builder()
                .memberDTO(MemberConverter.toMemberDTO(member))
                .challengePreviewDTO(ChallengeConverter.toChallengePreviewDTO(challenge, hostName))
                .build();
    }

    /***
     * 추천 챌린지 리스트를 DTO에 담아주기
     * @param recommendChallengeList 추천 챌린지 리스트
     * RecommendChallengeListDTO
     */
    public static ChallengeResponseDTO.RecommendChallengeListDTO toRecommendChallengeListDTO(List<ChallengeResponseDTO.RecommendChallengeDTO> recommendChallengeList) {
        return ChallengeResponseDTO.RecommendChallengeListDTO.builder()
                .recommendChallengeDTOList(recommendChallengeList)
                .build();
    }
}
