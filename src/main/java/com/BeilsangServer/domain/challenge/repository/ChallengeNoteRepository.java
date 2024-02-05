package com.BeilsangServer.domain.challenge.repository;

import com.BeilsangServer.domain.challenge.entity.ChallengeNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeNoteRepository extends JpaRepository<ChallengeNote, Long> {
    List<ChallengeNote> findAllByChallenge_Id(Long id);
}
