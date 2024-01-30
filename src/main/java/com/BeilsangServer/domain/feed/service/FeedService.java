package com.BeilsangServer.domain.feed.service;

import com.BeilsangServer.domain.challenge.converter.ChallengeConverter;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.repository.ChallengeRepository;
import com.BeilsangServer.domain.feed.converter.FeedConverter;
import com.BeilsangServer.domain.feed.dto.AddFeedRequestDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.domain.feed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private FeedRepository feedRepository;
    private FeedConverter feedConverter;
    private ChallengeRepository challengeRepository;

    /***
     * 피드 인증하기
     * @param addFeedRequestDTO
     * @param challengeId
     * @return 새로 추가된 feedDto
     */
    @Transactional
    public FeedDTO createFeed(AddFeedRequestDTO addFeedRequestDTO, Long challengeId){
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> {throw new IllegalArgumentException("없은챌린지다.");});
        Feed feed = feedConverter.toEntity(addFeedRequestDTO, challenge);

        feedRepository.save(feed);
        return feedConverter.entityToDto(feed);
    }

    /***
     * 피드 인증 가이드 조회
     * @param challengeId
     * @return guide dto
     */
    @Transactional(readOnly = true)
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
        FeedDTO feedDTO = feedConverter.entityToDto(feed);

        return feedDTO;
    }

    /***
     * 챌린지 이름 혹은 카테고리로 피드 검색하기
     * @param name
     * @return feedList dto
     */
//    public List<FeedDTO> searchFeed(String name){
//        List<Feed> feedList = feedRepository.findByTitleContaining(name);
//
//        return feedConverter.toDtoList(feedList);
//    }
}
