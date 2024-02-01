package com.BeilsangServer.domain.feed.repository;

import com.BeilsangServer.domain.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {

//    List<Feed> findByTitleContaining(String name);

    // Feed 테이블에서 Challenge_id 로 데이터 찾기
    List<Feed> findAllByChallenge_IdIn(List<Long> ChallengeId);
}
