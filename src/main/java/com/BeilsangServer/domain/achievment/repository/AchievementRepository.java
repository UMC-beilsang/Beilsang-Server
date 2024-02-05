package com.BeilsangServer.domain.achievment.repository;

import com.BeilsangServer.domain.achievment.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findAllByMember_Id(Long memberId);

    Integer findCountByMemberId(Long memberId);
}
