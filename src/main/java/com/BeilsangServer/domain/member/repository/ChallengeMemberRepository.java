package com.BeilsangServer.domain.member.repository;

import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.global.enums.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Long> {

    List<ChallengeMember> findAllByMemberId(Long memberId);
    Optional<ChallengeMember> findByMember_idAndChallenge_Id(Long memberId, Long challengeId);

    // 챌린지의 호스트 찾기
    Optional<ChallengeMember> findByChallengeIdAndIsHostIsTrue(Long challengeId);

    Long countByMember_Id(Long memberId);

    List<ChallengeMember> findAllByChallengeStatus(ChallengeStatus challengeStatus);

    List<ChallengeMember> findAllByChallengeStatusAndIsFeedUpload(ChallengeStatus challengeStatus, Boolean isFeedUpload);

    List<ChallengeMember> findAllByChallengeId(Long challengeId);
    int countByMemberIdAndChallengeStatus(Long memberId, ChallengeStatus challengeStatus);
}
