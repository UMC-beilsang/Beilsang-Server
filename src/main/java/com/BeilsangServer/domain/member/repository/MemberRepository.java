package com.BeilsangServer.domain.member.repository;

import com.BeilsangServer.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findByEmail(String email);

    void deleteBySocialId(Long id);

    Member findBySocialId(Long socialId);

    Member findByRefreshToken(String token);


    String findResolutionById(Long memberId);

    Integer findTotalPointById(Long memberId);

    boolean existsByNickName(String nickName);

    @Query("SELECT m FROM Member m " +
            "JOIN ChallengeMember cm ON m.id = cm.member.id " +
            "JOIN Feed f ON cm.id = f.challengeMember.id " +
            "WHERE f.id = :feedId")
    Member findMemberByFeedId(@Param("feedId") Long feedId);
}

