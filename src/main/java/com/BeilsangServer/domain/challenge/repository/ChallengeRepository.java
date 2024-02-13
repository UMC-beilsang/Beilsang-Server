package com.BeilsangServer.domain.challenge.repository;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.global.enums.Category;
import com.BeilsangServer.domain.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    // 아직 안시작한 챌린지 중 좋아요 상위 2개 찾음
    List<Challenge> findTop2ByStartDateAfterOrderByCountLikesDesc(LocalDate localDate);

    // 카테고리에 해당하는 모든 챌린지 리스트 형태로 반환
    List<Challenge> findAllByStartDateAfterAndCategoryOrderByAttendeeCountDesc(LocalDate localDate, Category category);

    List<Challenge> findAllByStartDateAfterOrderByAttendeeCountDesc(LocalDate localDate);

    List<Challenge> findByTitleContaining(String name);

    List<Challenge> findTop5ByCategoryOrderByCountLikesDesc(Category category);

    List<Challenge> findAllByIdInAndCategory(List<Long> challengeIds,Category category);
}
