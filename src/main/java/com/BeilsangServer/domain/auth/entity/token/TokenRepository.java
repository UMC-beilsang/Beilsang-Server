package com.BeilsangServer.domain.auth.entity.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findById(Long memberId);

    void deleteById(Long memberId);

}