package com.BeilsangServer.domain.category.repository;

import com.BeilsangServer.domain.category.entity.ChallengeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeCategoryRepository extends JpaRepository<ChallengeCategory, Long> {
}
