package com.BeilsangServer.domain.challenge.entity;

import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.domain.feed.entity.FeedLike;
import com.BeilsangServer.domain.like.entity.ChallengeLike;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.global.common.BaseEntity;
import com.BeilsangServer.global.enums.Category;
import com.BeilsangServer.global.enums.ChallengePeriod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Challenge extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String title;

    private LocalDate startDate;

    private LocalDate finishDate;

    private Integer joinPoint;

    private String mainImageUrl;

    private String certImageUrl;

    private String details;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChallengeNote> challengeNotes = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChallengeLike> challengeLikes = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Feed> feeds = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChallengeMember> challengeMembers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ChallengePeriod period;

    private Integer totalGoalDay;

    private Integer attendeeCount;

    private Integer countLikes;

    private Integer collectedPoint;


    // 연관관계 메서드
    public void setChallengeNotes(ChallengeNote note) {
        challengeNotes.add(note);
        note.setChallenge(this);
    }

    // 참여 인원 증가
    public void increaseAttendeeCount() {
        this.attendeeCount++;
    }

    public void increaseCountLikes(){this.countLikes++;}
    public void decreaseCountLikes(){this.countLikes--;}

    // 모인 포인트 증가
    public void addCollectedPoint(Integer point) {
        this.collectedPoint += point;
    }
}
