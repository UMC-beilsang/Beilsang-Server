package com.BeilsangServer.domain.challenge.converter;

import com.BeilsangServer.domain.challenge.dto.ChallengeRequestDTO;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.entity.ChallengeNote;
import com.BeilsangServer.domain.member.converter.MemberConverter;
import com.BeilsangServer.domain.member.entity.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ChallengeConverter {

    public static Challenge toChallenge(ChallengeRequestDTO.CreateChallengeDTO request, String mainImageUrl, String certImageUrl) {

        // 시작일을 포함하여 기간(7일/30일)만큼 지난 날짜를 챌린지 종료 날짜로 설정
        Integer period = request.getPeriod().getDays();
        LocalDate finishDate = request.getStartDate().plusDays(period - 1);

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
                .countLikes(0)
                .collectedPoint(request.getJoinPoint())
                .build();
    }

    public static ChallengeResponseDTO.ChallengeDTO toChallengeDTO(Challenge challenge, Integer dDay, String hostName, boolean like, Optional<Float> achieveRate) {

        List<String> challengeNotes = toStringChallengeNotes(challenge.getChallengeNotes());

        ChallengeResponseDTO.ChallengeDTO.ChallengeDTOBuilder builder = ChallengeResponseDTO.ChallengeDTO.builder()
                .attendeeCount(challenge.getAttendeeCount())
                .hostName(hostName)
                .createdDate(challenge.getCreatedAt().toLocalDate())
                .dayOfWeek(challenge.getCreatedAt().toLocalDate().getDayOfWeek())
                .imageUrl(challenge.getMainImageUrl())
                .certImageUrl(challenge.getCertImageUrl())
                .title(challenge.getTitle())
                .startDate(challenge.getStartDate())
                .category(challenge.getCategory())
                .details(challenge.getDetails())
                .challengeNotes(challengeNotes)
                .joinPoint(challenge.getJoinPoint())
                .dDay(dDay)
                .likes(challenge.getCountLikes())
                .like(like)
                .period(challenge.getPeriod())
                .totalGoalDay(challenge.getTotalGoalDay());

        achieveRate.ifPresent(rate -> {
            float roundedRate = Math.round(rate * 100) / 100.0f;
            builder.achieveRate(roundedRate);
        });

        return builder.build();
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

    public static ChallengeResponseDTO.MyChallengePreviewDTO toMyChallengePreviewDTO(Challenge challenge, Float achieveRate) {

        return ChallengeResponseDTO.MyChallengePreviewDTO.builder()
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .imageUrl(challenge.getMainImageUrl())
                .achieveRate(Math.round(achieveRate * 100) / 100.0f) // 소수점 아래 한자리까지만 보이도록
                .build();
    }
}
