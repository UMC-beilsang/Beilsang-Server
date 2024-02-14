package com.BeilsangServer.domain.feed.service;

import com.BeilsangServer.aws.s3.AmazonS3Manager;
import com.BeilsangServer.domain.challenge.converter.ChallengeConverter;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.entity.ChallengeNote;
import com.BeilsangServer.domain.challenge.repository.ChallengeNoteRepository;
import com.BeilsangServer.domain.challenge.repository.ChallengeRepository;
import com.BeilsangServer.domain.feed.FeedController;
import com.BeilsangServer.domain.feed.converter.FeedConverter;
import com.BeilsangServer.domain.feed.dto.AddFeedRequestDTO;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.domain.feed.entity.FeedLike;
import com.BeilsangServer.domain.feed.repository.FeedLikeRepository;
import com.BeilsangServer.domain.feed.repository.FeedRepository;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.ChallengeMemberRepository;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.BeilsangServer.domain.member.service.ChallengeMemberService;
import com.BeilsangServer.domain.uuid.entity.Uuid;
import com.BeilsangServer.domain.uuid.repository.UuidRepository;
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
    private final AmazonS3Manager s3Manager;
    private final FeedLikeRepository feedLikeRepository;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeNoteRepository challengeNoteRepository;
    private final UuidRepository uuidRepository;
    private final MemberRepository memberRepository;
    private final ChallengeMemberService challengeMemberService;


    /***
     * 피드 생성 (챌린지 인증하기)
     * @param request
     * @param challengeId
     * @param memberId
     * @return
     */
    @Transactional
    public Long createFeed(AddFeedRequestDTO.CreateFeedDTO request, Long challengeId, Long memberId){

        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> {throw new IllegalArgumentException("없는챌린지다.");});
        ChallengeMember challengeMember = challengeMemberRepository.findByMember_idAndChallenge_Id(memberId, challenge.getId());

        Uuid feedUuid = uuidRepository.save(Uuid.builder().uuid(UUID.randomUUID().toString()).build());
        String feedUrl = s3Manager.uploadFile(s3Manager.generateFeedKeyName(feedUuid), request.getFeedImage());

        Feed feed = feedConverter.toEntity(request, challenge, challengeMember, feedUrl);

        feedRepository.save(feed);

        challengeMemberService.checkFeedUpload(feed.getChallengeMember());

        return feed.getId();
    }

    /***
     * 피드 인증 가이드 조회
     * @param challengeId
     * @return guide dto
     */
    public ChallengeResponseDTO.ChallengeGuide getGuide(Long challengeId){
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(()->{throw new IllegalArgumentException("없는챌린지다.");});
        List<ChallengeNote> challengeNotes = challengeNoteRepository.findAllByChallenge_Id(challenge.getId());

        List<String> challengeNoteList = ChallengeConverter.toStringChallengeNotes(challengeNotes);
        ChallengeResponseDTO.ChallengeGuide challengeGuide = ChallengeResponseDTO.ChallengeGuide.builder()
                .certImage(challenge.getCertImageUrl())
                .challengeNoteList(challengeNoteList)
                .build();

        return challengeGuide;
    }

    /***
     * 선택한 피드 정보 조회
     * @param feedId
     * @return feed dto
     *
     */
    public FeedDTO getFeed(Long feedId, Long memberId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> {
            throw new IllegalArgumentException("이런피드없다.");
        });

        Member member = memberRepository.findMemberByFeedId(feedId); // 게시한 사용자의 정보
        Long feedLikes = feedLikeRepository.countByFeed_Id(feedId); // 피드 좋아요 개수
        boolean feedLike = feedLikeRepository.existsByFeed_IdAndMember_Id(feedId,memberId); // 현재 사용자가 해당 피드 좋아요를 눌렀는지 안 눌렀는지 정보

        FeedDTO feedDTO = feedConverter.entityToDtoIncludeLikes(feed,feedLikes,feedLike,member);

        return feedDTO;
    }

    /***
     * 챌린지 이름으로 챌린지 및 피드 검색하기
     * @param name
     * @return previewChallengeAndFeed dto
     */
    public FeedDTO.previewChallengeAndFeed searchChallengeAndFeed(String name){
        List<Challenge> challengeList = challengeRepository.findByTitleContaining(name);

        List<Long> challengeIds = new ArrayList<>();
        for(Challenge c : challengeList){
            challengeIds.add(c.getId());
        }

        List<Feed> feedList = feedRepository.findAllByChallenge_IdIn(challengeIds);

        List<ChallengeResponseDTO.ChallengePreviewDTO> challengeDTOList = challengeList.stream()
                .map(challenge -> ChallengeConverter.toChallengePreviewDTO(challenge,getHostName(challenge.getId())))
                .toList();

        List<FeedDTO.previewFeedDto> feedDTOList = feedList.stream()
                .map(feed -> feedConverter.toPreviewFeedDto(feed))
                .toList();

        return FeedDTO.previewChallengeAndFeed.builder()
                .challenges(challengeDTOList)
                .feeds(feedDTOList)
                .build();
    }

    /***
     * 피드 좋아요 하기
     * @param feedId
     * @return 좋아요한 feedId
     */
    @Transactional
    public Long feedLike(Long feedId, Long memberId){
        Feed feed = feedRepository.findById(feedId).orElseThrow(()-> {throw new IllegalArgumentException("없다");});
        Member member = memberRepository.findById(memberId).orElseThrow(()->{throw new IllegalArgumentException("없다");});

        FeedLike feedLike = FeedLike.builder()
                .feed(feed)
                .member(member)
                .build();

        feedLikeRepository.save(feedLike);

        return feedLike.getId();
    }

    /***
     * 피드 좋아요 취소하기
     * @param feedId
     * @return 좋아요 취소한 feedId
     */
    @Transactional
    public Long feedUnLike(Long feedId,Long memberId){
        FeedLike feedLike = feedLikeRepository.findByFeed_IdAndMember_Id(feedId,memberId);

        feedLikeRepository.delete(feedLike);

        return feedLike.getId();
    }

    /***
     * 카테고리로 피드 조회하기
     * @param category
     * @return 주어진 카테고리에 해당하는 feedDtoList
     */
    public FeedDTO.previewFeedListDto getFeedByCategory(String category){
        Category categoryByEnum = Category.from(category);

        List<Feed> feedList;
        if (categoryByEnum.equals(Category.ALL)){
            feedList = feedRepository.findAll();
        }
        else{
            feedList = feedRepository.findAllByChallenge_Category(categoryByEnum); // 시간 순으로 정렬
        }

        FeedDTO.previewFeedListDto feedDTOList = feedConverter.toPreviewFeedListDto(feedList);

        return feedDTOList;
    }

    /***
     * status 와 category 로 나의 챌린지 피드 조회하기
     * @param status
     * @param category
     * @param memberId
     * @return 필터링된 feedDtoList
     */
    public FeedDTO.previewFeedListDto getFeedByStatusAndCategory(String status, String category, Long memberId){

        Category categoryByEnum = Category.from(category);

        // memberId로 그 member와 관련된 챌린지 정보 가져오기
        List<ChallengeMember> challengeMembers = challengeMemberRepository.findAllByMember_id(memberId);
        LocalDate now = LocalDate.now();

        List<Long> challengeIds = new ArrayList<>();

        // 상태 & 카테고리 한번에 처리
        for (ChallengeMember c : challengeMembers) {
            if ("참여중".equals(status) && isAfterOrEqual(c.getChallenge().getFinishDate(),now) &&
                    (categoryByEnum.equals(Category.ALL) || categoryByEnum.equals(c.getChallenge().getCategory()))) {
                challengeIds.add(c.getChallenge().getId());
            } else if ("등록한".equals(status) && c.getIsHost() &&
                    (categoryByEnum.equals(Category.ALL) || categoryByEnum.equals(c.getChallenge().getCategory()))) {
                challengeIds.add(c.getChallenge().getId());
            } else if ("완료된".equals(status) && c.getChallenge().getFinishDate().isBefore(now) &&
                    (categoryByEnum.equals(Category.ALL) || categoryByEnum.equals(c.getChallenge().getCategory()))) {
                challengeIds.add(c.getChallenge().getId());
            }
        }

        List<Feed> feedList = feedRepository.findAllByChallenge_IdIn(challengeIds);

        FeedDTO.previewFeedListDto feedDTOList = feedConverter.toPreviewFeedListDto(feedList);

        return feedDTOList;
    }

    public String getHostName(Long challengeId) {

        Member host = challengeMemberRepository.findByChallenge_IdAndIsHostIsTrue(challengeId).getMember();
        return host.getNickName();
    }

    public static boolean isAfterOrEqual(LocalDate date1, LocalDate date2){
        return !date1.isBefore(date2);
    }
}
