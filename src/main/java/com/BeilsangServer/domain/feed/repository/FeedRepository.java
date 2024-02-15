package com.BeilsangServer.domain.feed.repository;

import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.global.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {


    // Feed 테이블에서 Challenge_id 로 데이터 찾기
    List<Feed> findAllByChallenge_IdIn(List<Long> ChallengeId);

    Long countByChallengeMember_IdIn(List<Long> challengeMemberIds);

    List<Feed> findTop4ByChallengeMember_IdInOrderByCreatedAtDesc(List<Long> challengeMemberIds);

    Page<Feed> findAllByChallenge_Category(Category category, Pageable pageable);

}
