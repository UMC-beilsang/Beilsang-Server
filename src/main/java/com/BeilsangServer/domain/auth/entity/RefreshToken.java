package com.BeilsangServer.domain.auth.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id
    private String keyId;

    private String value;

    @Builder
    public RefreshToken(String keyId, String value) {
        this.keyId = keyId;
        this.value = value;
    }

    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }
}