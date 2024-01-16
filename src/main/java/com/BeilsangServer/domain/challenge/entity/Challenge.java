package com.BeilsangServer.domain.challenge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    private String name;

    private LocalDate startDate;

    private LocalDate finishDate;
    private String startDay;

    private int joinPoint;

    private Boolean isPrivate;

    private String certImageUrl;

    private String details;

    @Enumerated(EnumType.STRING)
    private ChallengeCycle cycle;

    private int frequency;

    private int totalGoal;
}
