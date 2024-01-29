package com.BeilsangServer.domain.challenge.service;

import com.BeilsangServer.domain.challenge.converter.ChallengeConverter;
import com.BeilsangServer.domain.challenge.dto.ChallengeRequestDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.entity.ChallengeNote;
import com.BeilsangServer.domain.challenge.repository.ChallengeNoteRepository;
import com.BeilsangServer.domain.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeNoteRepository challengeNoteRepository;
    private final ChallengeRepository challengeRepository;

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

    public Challenge getChallenge(Long challengeId) {

        return challengeRepository.findById(challengeId).get();
    }
}
