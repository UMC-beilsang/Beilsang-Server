package com.BeilsangServer.domain.member.repository;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Long> {

    List<ChallengeMember> findAllByMember_id(Long memberId);

    // 챌린지의 호스트 찾기
    ChallengeMember findByChallenge_IdAndIsHostIsTrue(Long challengeId);

    Long countByMember_Id(Long memberId);

}
