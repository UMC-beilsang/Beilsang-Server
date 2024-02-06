package com.BeilsangServer.domain.feed.converter;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.feed.dto.AddFeedRequestDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.entity.Feed;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
                .category(feed.getChallenge().getCategory())
                .build();
    }

    public FeedDTO entityToDtoIncludeLikes(Feed feed,Long likes){
        return FeedDTO.builder()
                .id(feed.getId())
                .review(feed.getReview())
                .uploadDate(feed.getUploadDate())
                .feedUrl(feed.getFeedUrl())
                .challengeTitle(feed.getChallenge().getTitle())
                .category(feed.getChallenge().getCategory())
                .likes(likes)
                .build();
    }


    public List<FeedDTO> toDtoList(List<Feed> feedList){
        List<FeedDTO> dtoList = new ArrayList<>();

        for (Feed f : feedList){
            dtoList.add(entityToDto(f));
        }
        return dtoList;
    }

    public FeedDTO.previewFeedListDto toPreviewFeedListDto(List<Feed> feedList){

        List<FeedDTO.previewFeedDto> feedDtos = feedList.stream().map(feed -> FeedDTO.previewFeedDto.builder()
                .feedId(feed.getId())
                .feedUrl(feed.getFeedUrl())
                .build()
        ).toList();

        return FeedDTO.previewFeedListDto.builder().feeds(feedDtos).build();
    }
}
