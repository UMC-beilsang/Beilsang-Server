package com.BeilsangServer.domain.member.repository;

import com.BeilsangServer.domain.member.entity.ChallengeMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Long> {
}
