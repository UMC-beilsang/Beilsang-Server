package com.BeilsangServer.domain.feed.converter;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.feed.dto.AddFeedRequestDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.entity.Feed;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FeedConverter {

    public Feed toEntity(AddFeedRequestDTO addFeedRequestDTO, Challenge challenge) {
        return Feed.builder()
                .uploadDate(addFeedRequestDTO.getUploadDate())
                .review(addFeedRequestDTO.getReview())
                .feedUrl(addFeedRequestDTO.getFeedUrl())
                .challenge(challenge)
                .build();
    }

    public FeedDTO entityToDto(Feed feed){
        return FeedDTO.builder()
                .id(feed.getId())
                .review(feed.getReview())
                .uploadDate(feed.getUploadDate())
                .feedUrl(feed.getFeedUrl())
                .challengeTitle(feed.getChallenge().getTitle())
                .build();
    }

//    public Feed toEntity()
}
