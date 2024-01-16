package com.BeilsangServer.domain.feed.entity;

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
public class FeedImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_image_id")
    private Long id;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

}
