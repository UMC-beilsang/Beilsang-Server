package com.BeilsangServer.domain.feed.service;

import com.BeilsangServer.aws.s3.AmazonS3Manager;
import com.BeilsangServer.domain.challenge.converter.ChallengeConverter;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.repository.ChallengeRepository;
import com.BeilsangServer.domain.feed.converter.FeedConverter;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.domain.feed.entity.FeedLike;
import com.BeilsangServer.domain.feed.repository.FeedLikeRepository;
import com.BeilsangServer.domain.feed.repository.FeedRepository;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.domain.member.repository.ChallengeMemberRepository;
import com.BeilsangServer.global.enums.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedConverter feedConverter;
    private final ChallengeRepository challengeRepository;
    private final AmazonS3Manager amazonS3Manager;
    private final FeedLikeRepository feedLikeRepository;
    private final ChallengeMemberRepository challengeMemberRepository;


    /***
     * 피드 인증하기
     * @param file, review, challengeId
     * @param challengeId
     * @return 새로 추가된 feedDto
     * challengeMember 추가 필요
     */
    @Transactional
    public FeedDTO createFeed(MultipartFile file, String review, Long challengeId){
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> {throw new IllegalArgumentException("없는챌린지다.");});

        String uuid = UUID.randomUUID().toString();
        String feedUrl = amazonS3Manager.uploadFile(uuid,file);
        Feed feed = Feed.builder()
                .feedUrl(feedUrl)
                .review(review)
                .uploadDate(LocalDate.now())
                .challenge(challenge)
                .build();
        feedRepository.save(feed);
        return feedConverter.entityToDto(feed);
    }

    /***
     * 피드 인증 가이드 조회
     * @param challengeId
     * @return guide dto
     */
    public ChallengeResponseDTO.CreateResultDTO getGuide(Long challengeId){
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(()->{throw new IllegalArgumentException("없는챌린지다.");});
        ChallengeResponseDTO.CreateResultDTO guide = ChallengeConverter.toGuideResultDto(challenge);

        return guide;
    }

    /***
     * 선택한 피드 정보 조회
     * @param feedId
     * @return feed dto
     */
    public FeedDTO getFeed(Long feedId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> {
            throw new IllegalArgumentException("이런피드없다.");
        });
        Long feedLikes = feedLikeRepository.countByFeed_Id(feedId);

        FeedDTO feedDTO = feedConverter.entityToDtoIncludeLikes(feed,feedLikes);

        return feedDTO;
    }

    /***
     * 챌린지 이름으로 피드 검색하기
     * @param name
     * @return feedList dto
     */
    public List<FeedDTO> searchFeed(String name){
        List<Challenge> challengeList = challengeRepository.findByTitleContaining(name);

        List<Long> challengeIds = new ArrayList<>();
        for(Challenge c : challengeList){
            challengeIds.add(c.getId());
        }

        List<Feed> feedList = feedRepository.findAllByChallenge_IdIn(challengeIds);

        List<FeedDTO> feedDTOList = new ArrayList<>();
        for (Feed f : feedList){
            feedDTOList.add(feedConverter.entityToDto(f));
        }

        return feedDTOList;
    }

    /***
     * 피드 좋아요 하기
     * @param feedId
     * @return 좋아요한 feedId
     */
    @Transactional
    public Long feedLike(Long feedId){
        Feed feed = feedRepository.findById(feedId).orElseThrow(()-> {throw new IllegalArgumentException("없다");});

        FeedLike feedLike = FeedLike.builder()
                .feed(feed)
                .build();
        feedLikeRepository.save(feedLike);

        return feedLike.getId();
    }

    /***
     * 피드 좋아요 취소하기
     * @param feedId
     * @return 좋아요 취소한 feedId
     * findByIdAndMember_id(id,memberId)
     */
    @Transactional
    public Long feedUnLike(Long feedId){
        FeedLike feedLike = feedLikeRepository.findById(feedId).orElseThrow(()->{throw new IllegalArgumentException("좋아요없다");});

        feedLikeRepository.delete(feedLike);

        return feedLike.getId();
    }

    /***
     * 카테고리로 피드 조회하기
     * @param category
     * @return 주어진 카테고리에 해당하는 feedDtoList
     */
    public List<FeedDTO> getFeedByCategory(String category){
        Category categoryByEnum = Category.valueOf(category);

        List<Feed> feedList = feedRepository.findAllByChallenge_Category(categoryByEnum);

        List<FeedDTO> feedDTOList = new ArrayList<>();
        for (Feed f : feedList){
            feedDTOList.add(feedConverter.entityToDto(f));
        }

        return feedDTOList;
    }

    /***
     * status 와 category 로 챌린지 피드 조회하기
     * @param status
     * @param category
     * @param memberId
     * @return 필터링된 feedDtoList
     */
    public List<FeedDTO> getFeedByStatusAndCategory(String status, String category, Long memberId){
        Category categoryByEnum = Category.valueOf(category);

        // memberId로 그 member와 관련된 챌린지 정보 가져오기
        List<ChallengeMember> challengeMembers = challengeMemberRepository.findAllByMember_id(memberId);
        LocalDate now = LocalDate.now();

        List<Long> challengeIds = new ArrayList<>();
//        if (status == "참여중"){
//            for (ChallengeMember c : challengeMembers){
//                if(c.getChallenge().getFinishDate().isAfter(now)) {
//                    challengeIds.add(c.getChallenge().getId());
//                }
//            }
//            List<Challenge> challengeList = challengeRepository.findAllById(challengeIds);
//        } else if (status == "등록한") {
//            for(ChallengeMember c : challengeMembers){
//                if (c.getIsHost()){
//                    challengeIds.add(c.getChallenge().getId());
//                }
//            }
//        } else if(status == "완료된"){
//            for (ChallengeMember c : challengeMembers){
//                if (c.getChallenge().getFinishDate().isBefore(now)){
//                    challengeIds.add(c.getChallenge().getId());
//                }
//            }
//        }
        // 상태 & 카테고리 한번에 처리
        for (ChallengeMember c : challengeMembers) {
            if ("참여중".equals(status) && c.getChallenge().getFinishDate().isAfter(now) && categoryByEnum.equals(c.getChallenge().getCategory())) {
                challengeIds.add(c.getChallenge().getId());
            } else if ("등록한".equals(status) && c.getIsHost() && categoryByEnum.equals(c.getChallenge().getCategory())) {
                challengeIds.add(c.getChallenge().getId());
            } else if ("완료된".equals(status) && c.getChallenge().getFinishDate().isBefore(now) && categoryByEnum.equals(c.getChallenge().getCategory())) {
                challengeIds.add(c.getChallenge().getId());
            }
        }

        List<Feed> feedList = feedRepository.findAllByChallenge_IdIn(challengeIds);

        List<FeedDTO> feedDTOList = new ArrayList<>();
        for (Feed f : feedList){
            feedDTOList.add(feedConverter.entityToDto(f));
        }

        return feedDTOList;
    }
}
