package com.BeilsangServer.domain.challenge.repository;

import com.BeilsangServer.domain.challenge.entity.ChallengeNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeNoteRepository extends JpaRepository<ChallengeNote, Long> {
}
