package com.BeilsangServer.domain.auth.entity.token;


import com.BeilsangServer.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Setter
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String grantType;

    private String accessToken;
    private Long accessTokenExpirationTime;

    private String refreshToken;
    private Long refreshTokenExpirationTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}