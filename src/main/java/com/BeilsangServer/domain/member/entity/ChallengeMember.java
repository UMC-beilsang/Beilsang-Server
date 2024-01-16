package com.BeilsangServer.domain.member.entity;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_member_id")
    private Long id;

    private Boolean isHost;

    private int goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

}
