package com.BeilsangServer.domain.feed.converter;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.feed.dto.AddFeedRequestDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.domain.member.entity.Member;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class FeedConverter {

    public Feed toEntity(AddFeedRequestDTO.CreateFeedDTO request, Challenge challenge, ChallengeMember challengeMember, String feedImageRul) {
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

    public FeedDTO entityToDtoIncludeLikes(Feed feed,Long likes,boolean like,Member member){
        return FeedDTO.builder()
                .id(feed.getId())
                .review(feed.getReview())
                .uploadDate(feed.getUploadDate())
                .feedUrl(feed.getFeedUrl())
                .challengeTitle(feed.getChallenge().getTitle())
                .category(feed.getChallenge().getCategory())
                .likes(likes)
                .like(like)
                .day(ChronoUnit.DAYS.between(feed.getUploadDate(),LocalDate.now()))
                .nickName(member.getNickName())
                .profileImage(member.getProfileUrl())
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
                .day(ChronoUnit.DAYS.between(feed.getUploadDate(),LocalDate.now()))
                .build();
    }

    public FeedDTO.previewFeedListDto toPreviewFeedListDto(List<Feed> feedList){

        List<FeedDTO.previewFeedDto> feedDtos = feedList.stream().map(feed -> FeedDTO.previewFeedDto.builder()
                .feedId(feed.getId())
                .feedUrl(feed.getFeedUrl())
                .day(ChronoUnit.DAYS.between(feed.getUploadDate(),LocalDate.now()))
                .build()
        ).toList();

        return FeedDTO.previewFeedListDto.builder().feeds(feedDtos).build();
    }
}
