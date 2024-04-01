package com.BeilsangServer.domain.auth.apple.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class ApplePublicKeys {

    private List<ApplePublicKey> keys;

    public ApplePublicKeys(List<ApplePublicKey> keys) {
        this.keys = List.copyOf(keys);
    }

    // alg와 kid를 파라미터로 받아 일치하는 키를 찾는 메서드

    public ApplePublicKey getMatchingKey(String alg, String kid) {
        return this.keys
                .stream()
                .filter(k -> k.getAlg().equals(alg) && k.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Apple JWT 값의 alg, kid 정보가 올바르지 않습니다."));
    }


}