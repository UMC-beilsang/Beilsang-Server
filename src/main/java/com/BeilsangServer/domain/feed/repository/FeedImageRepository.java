package com.BeilsangServer.domain.feed.repository;

import com.BeilsangServer.domain.feed.entity.FeedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {
}
