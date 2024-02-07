package com.BeilsangServer.domain.feed.repository;

import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.domain.feed.entity.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedLikeRepository extends JpaRepository<FeedLike,Long> {
    Long countByFeed_Id(Long feedId);

    Long countByMember_Id(Long memberId);
}
