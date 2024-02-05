package com.BeilsangServer.domain.auth.repository;

import com.BeilsangServer.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByKeyId(String key);
    void deleteByKeyId(String key);
}