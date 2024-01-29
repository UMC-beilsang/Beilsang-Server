package com.BeilsangServer.domain.challenge.entity;

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
public class Challenge {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    private String title;

    private LocalDate startDate;

    private LocalDate finishDate;

    private String startDay;

    private Long joinPoint;

    private String imageUrl;

    private String certImageUrl;

    private String details;

    @OneToMany(mappedBy = "challenge")
    private List<ChallengeNotification> notifications = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ChallengePeriod period;

    private Integer totalGoal;
}
