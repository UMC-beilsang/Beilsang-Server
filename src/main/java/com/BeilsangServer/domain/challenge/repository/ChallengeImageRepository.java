package com.BeilsangServer.domain.challenge.repository;

import com.BeilsangServer.domain.challenge.entity.ChallengeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeImageRepository extends JpaRepository<ChallengeImage, Long> {
}
