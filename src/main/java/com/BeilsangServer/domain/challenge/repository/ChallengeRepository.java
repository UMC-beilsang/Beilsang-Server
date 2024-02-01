package com.BeilsangServer.domain.challenge.repository;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    // 아직 안시작한 챌린지 중 좋아요 상위 2개 찾음
    List<Challenge> findTop2ByStartDateAfterOrderByCountLikesDesc(LocalDate localDate);

    List<Challenge> findByTitleContaining(String name);
}
