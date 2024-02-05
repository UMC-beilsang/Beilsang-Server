package com.BeilsangServer.domain.member.service;


import com.BeilsangServer.domain.achievment.repository.AchievementRepository;
import com.BeilsangServer.domain.challenge.repository.ChallengeRepository;
import com.BeilsangServer.domain.feed.converter.FeedConverter;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.domain.feed.repository.FeedLikeRepository;
import com.BeilsangServer.domain.feed.repository.FeedRepository;
import com.BeilsangServer.domain.member.dto.MemberDto;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.ChallengeMemberRepository;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final FeedRepository feedRepository;
    private final AchievementRepository achievementRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedConverter feedConverter;

    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    /***
     * 마이페이지 조회
     * @return
     */
    public MemberDto.myPageDTO getMyPage(){
        Long memberId = 1L;

        Member member = memberRepository.findById(memberId).orElseThrow(()->{throw new IllegalArgumentException("멤버없다");});

        // 피드 개수 : 챌린지멤버 id를 얻고,,,, 그 아이디들에 해당하는 피드 개수 구하기
        List<Long> challengeMemberIds = challengeMemberRepository.findAllChallengeMemberIdByMember_id(memberId);

        Long feedNum = feedRepository.countByChallengeMember_IdIn(challengeMemberIds);

        // 달성한 챌린지 : achievement 테이블에서 개수 가져오기,,, 카테고리로 필터링 안하고 그냥 회원id 같은거 모두 카운트
        Integer achievementNum = achievementRepository.findCountByMemberId(memberId);

        // 실패한 챌린지 : ,, 이건 우짜지?

        // 다짐 : 회원 테이블에서 가져오기
        String resolution = member.getResolution();

        // 챌린지 개수 : 멤버 아이디로 챌린지멤버 테이블 카운트
        Long challengeNum = challengeMemberRepository.countByMember_Id(memberId);

        // 찜 개수 : 회원 아이디로 챌린지라이크 테이블 접근해서 카운트
        Long likes = feedLikeRepository.countByMember_Id(memberId);

        // 현재 보유 포인트 : 회원 테이블에서 가져오기
        Integer points = member.getTotalPoint();

        // 피드,, 는 찾기,,,, : 최근 4개 피드만 반환
        List<Feed> feeds = feedRepository.findTop4ByChallengeMember_IdInOrderByCreatedAtDesc(challengeMemberIds);
        FeedDTO.previewFeedListDto feedDto = feedConverter.toPreviewFeedListDto(feeds);

        return MemberDto.myPageDTO.builder()
                .achieve(achievementNum)
                .likes(likes)
                .points(points)
                .feedNum(feedNum)
                .resolution(resolution)
                .challenges(challengeNum)
                .fail(0)
                .feedDTOs(feedDto)
                .build();
    }

}