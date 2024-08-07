package com.BeilsangServer.domain.feed.repository;

import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.domain.feed.entity.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedLikeRepository extends JpaRepository<FeedLike,Long> {
    Long countByFeed_Id(Long feedId);

    FeedLike findByFeed_Id(Long feedId);

    boolean existsByFeed_IdAndMember_Id(Long feedId, Long memberId);

    FeedLike findByFeed_IdAndMember_Id(Long feedId, Long memberId);
}
