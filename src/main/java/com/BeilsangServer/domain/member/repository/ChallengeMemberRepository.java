package com.BeilsangServer.domain.member.repository;

import com.BeilsangServer.domain.member.entity.ChallengeMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Long> {

    List<ChallengeMember> findAllByMember_id(Long memberId);


    Long countByMember_Id(Long memberId);


}
