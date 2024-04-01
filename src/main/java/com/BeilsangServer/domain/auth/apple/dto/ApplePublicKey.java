package com.BeilsangServer.domain.auth.apple.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
Apple 공개 키를 구성할 정보
 */
@Getter
public class ApplePublicKey {

    private final String kty; // RSA란 값으로 고정

    private final String kid;

    private final String use;

    private final String alg;

    private final String n; // RSA 공개키 생성에 사용

    private final String e; // RSA 공개키 생성에 사용

    public boolean isSameAlg(final String alg) {
        return this.alg.equals(alg);
    }

    public boolean isSameKid(final String kid) {
        return this.kid.equals(kid);
    }

    @JsonCreator
    public ApplePublicKey(@JsonProperty("kty") final String kty,
                          @JsonProperty("kid") final String kid,
                          @JsonProperty("use") final String use,
                          @JsonProperty("alg") final String alg,
                          @JsonProperty("n") final String n,
                          @JsonProperty("e") final String e) {
        this.kty = kty;
        this.kid = kid;
        this.use = use;
        this.alg = alg;
        this.n = n;
        this.e = e;
    }
}