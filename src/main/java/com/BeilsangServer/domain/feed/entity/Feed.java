package com.BeilsangServer.domain.feed.entity;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.feed.dto.AddFeedRequestDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feed {
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

//    public static Feed of(AddFeedRequestDTO addFeedRequestDTO, Challenge challenge){
//        return Feed.builder()
//                .challenge(challenge)
//                .challengeMember(challenge.)
//    }
}
