package com.BeilsangServer.domain.member.repository;

import com.BeilsangServer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findByEmail(String email);

    String findResolutionById(Long memberId);

    Integer findTotalPointById(Long memberId);

}

