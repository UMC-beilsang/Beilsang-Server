package com.BeilsangServer.domain.feed.entity;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.global.common.BaseEntity;
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
public class Feed extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id;

    private String review;

    private LocalDate uploadDate;

    private String feedUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_member_id")
    private ChallengeMember challengeMember;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FeedLike> feedLikes = new ArrayList<>();

}
