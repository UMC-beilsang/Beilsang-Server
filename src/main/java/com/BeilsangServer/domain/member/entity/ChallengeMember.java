package com.BeilsangServer.domain.member.entity;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.global.common.BaseEntity;
import com.BeilsangServer.global.enums.ChallengeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.http.auth.ChallengeState;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeMember extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_member_id")
    private Long id;

    private Boolean isHost;

    private Integer successDays;

    private ChallengeStatus challengeStatus;

    private Boolean isFeedUpload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    // 피드 업로드 상태 수정
    public void makeIsFeedUploadFalse() {
        this.isFeedUpload = false;
    }

    // 챌린지 상태 변경 메서드
    public void updateChallengeStatus(ChallengeStatus status) {
        this.challengeStatus = status;
    }

    // 성공 일수 증가 메서드
    public void increaseSuccessDays() {
        this.successDays++;
    }
}
