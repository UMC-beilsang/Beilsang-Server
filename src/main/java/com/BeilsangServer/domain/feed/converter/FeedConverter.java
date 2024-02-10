package com.BeilsangServer.domain.feed.converter;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.feed.dto.AddFeedRequestDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class FeedConverter {

    public Feed toEntity(AddFeedRequestDTO.CreateDTO request, Challenge challenge, ChallengeMember challengeMember, String feedImageRul) {
        return Feed.builder()
                .uploadDate(LocalDate.now())
                .review(request.getReview())
                .feedUrl(feedImageRul)
                .challenge(challenge)
                .challengeMember(challengeMember)
                .build();
    }

//    public FeedDTO entityToDto(Feed feed){
//        return FeedDTO.builder()
//                .id(feed.getId())
//                .review(feed.getReview())
//                .uploadDate(feed.getUploadDate())
//                .feedUrl(feed.getFeedUrl())
//                .challengeTitle(feed.getChallenge().getTitle())
//                .category(feed.getChallenge().getCategory())
//                .build();
//    }

    public FeedDTO entityToDtoIncludeLikes(Feed feed,Long likes,boolean like){
        return FeedDTO.builder()
                .id(feed.getId())
                .review(feed.getReview())
                .uploadDate(feed.getUploadDate())
                .feedUrl(feed.getFeedUrl())
                .challengeTitle(feed.getChallenge().getTitle())
                .category(feed.getChallenge().getCategory())
                .likes(likes)
                .like(like)
                .build();
    }


//    public List<FeedDTO> toDtoList(List<Feed> feedList){
//        List<FeedDTO> dtoList = new ArrayList<>();
//
//        for (Feed f : feedList){
//            dtoList.add(entityToDto(f));
//        }
//        return dtoList;
//    }

    public FeedDTO.previewFeedDto toPreviewFeedDto(Feed feed){
        return FeedDTO.previewFeedDto.builder()
                .feedId(feed.getId())
                .feedUrl(feed.getFeedUrl())
                .build();
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
