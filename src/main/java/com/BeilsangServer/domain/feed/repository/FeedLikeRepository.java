package com.BeilsangServer.domain.feed.repository;

import com.BeilsangServer.domain.feed.entity.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedLikeRepository extends JpaRepository<FeedLike,Long> {
}
