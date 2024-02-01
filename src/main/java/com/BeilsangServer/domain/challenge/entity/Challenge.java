package com.BeilsangServer.domain.challenge.entity;

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

    private String imageUrl;

    private String certImageUrl;

    private String details;

    @OneToMany(mappedBy = "challenge")
    private List<ChallengeNote> challengeNotes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ChallengePeriod period;

    private Integer totalGoalDay;

    private Integer attendeeCount;

    private Integer countLikes;


    // 연관관계 메서드
    public void setChallengeNotes(ChallengeNote note) {
        challengeNotes.add(note);
        note.setChallenge(this);
    }
}
