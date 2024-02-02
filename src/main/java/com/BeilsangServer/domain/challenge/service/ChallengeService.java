package com.BeilsangServer.domain.challenge.service;

import com.BeilsangServer.domain.challenge.converter.ChallengeConverter;
import com.BeilsangServer.domain.challenge.dto.ChallengeRequestDTO;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.entity.ChallengeNote;
import com.BeilsangServer.domain.challenge.repository.ChallengeNoteRepository;
import com.BeilsangServer.domain.challenge.repository.ChallengeRepository;
import com.BeilsangServer.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeNoteRepository challengeNoteRepository;
    private final ChallengeRepository challengeRepository;

    @Transactional
    public Challenge createChallenge(ChallengeRequestDTO.CreateDTO request) {

        // 컨버터를 사용해 DTO를 챌린지 엔티티로 변환
        Challenge challenge = ChallengeConverter.toChallenge(request);

        // 리스트로 받은 리스트 데이터를 반복문을 통해 ChallengeNote 엔티티 각각에 담고 저장
        List<String> notes = request.getNotes();
        for (String note : notes) {
            ChallengeNote challengeNote = ChallengeNote.builder().note(note).challenge(challenge).build();
            challengeNoteRepository.save(challengeNote);
        }

        return challengeRepository.save(challenge);
    }

    public ChallengeResponseDTO.GetChallengeDTO getChallenge(Long challengeId) {

        Challenge challenge = challengeRepository.findById(challengeId).get();

        Integer dDay = (int) LocalDate.now().until(challenge.getStartDate(), ChronoUnit.DAYS);

        return ChallengeConverter.toGetChallengeDTO(challenge, dDay, getRecommendChallenges());
    }

    public List<ChallengeResponseDTO.RecommendChallengeDTO> getRecommendChallenges() {

        // JPA를 사용해 아직 시작 안한 챌린지 중 좋아요 많은 2개를 리스트로 만들어 반환한다
        List<Challenge> recommendChallenges = challengeRepository.findTop2ByStartDateAfterOrderByCountLikesDesc(LocalDate.now());

        return recommendChallenges.stream()
                .map(challenge -> ChallengeResponseDTO.RecommendChallengeDTO.builder()
                        .challengeId(challenge.getId())
                        .imageUrl(challenge.getImageUrl())
                        .title(challenge.getTitle())
                        .category(challenge.getCategory())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ChallengeResponseDTO.GetChallengeByCategoryDTO> getChallengeByCategory(String stringCategory) {

        Category category = Category.from(stringCategory);
        List<Challenge> challenges = challengeRepository.findAllByCategory(category);

        return ChallengeConverter.toChallengeByCategoryDTO(challenges);
    }
}
