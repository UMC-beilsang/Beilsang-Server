package com.BeilsangServer.domain.like.repository;

import com.BeilsangServer.domain.like.entity.ChallengeLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeLikeRepository extends JpaRepository<ChallengeLike, Long> {
    List<ChallengeLike> findAllByMember_Id(Long memberId);

    Long countByMember_Id(Long memberId);

    ChallengeLike findByChallengeIdAndMemberId(Long challengeId, Long memberId);

    Boolean existsByChallengeIdAndMemberId(Long challengeId, Long memberId);
}
