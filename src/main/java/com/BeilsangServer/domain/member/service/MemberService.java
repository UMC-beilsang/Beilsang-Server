package com.BeilsangServer.domain.member.service;


import com.BeilsangServer.aws.s3.AmazonS3Manager;
import com.BeilsangServer.domain.achievment.entity.Achievement;
import com.BeilsangServer.domain.achievment.repository.AchievementRepository;
import com.BeilsangServer.domain.feed.converter.FeedConverter;
import com.BeilsangServer.domain.feed.dto.FeedDTO;
import com.BeilsangServer.domain.feed.entity.Feed;
import com.BeilsangServer.domain.feed.repository.FeedLikeRepository;
import com.BeilsangServer.domain.feed.repository.FeedRepository;
import com.BeilsangServer.domain.member.converter.MemberConverter;
import com.BeilsangServer.domain.member.dto.MemberResponseDTO;
import com.BeilsangServer.domain.member.dto.MemberUpdateDto;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.ChallengeMemberRepository;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.BeilsangServer.domain.point.converter.PointConverter;
import com.BeilsangServer.domain.point.dto.PointResponseDTO;
import com.BeilsangServer.domain.point.entity.PointLog;
import com.BeilsangServer.domain.point.repository.PointLogRepository;
import com.BeilsangServer.domain.uuid.entity.Uuid;
import com.BeilsangServer.domain.uuid.repository.UuidRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private final PointLogRepository pointLogRepository;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    /***
     * 마이페이지 조회
     * @return
     */
    public MemberResponseDTO.myPageDTO getMyPage(Long memberId){

        Member member = memberRepository.findById(memberId).orElseThrow(()->{throw new IllegalArgumentException("멤버없다");});

        // 피드 개수 : 챌린지멤버 id를 얻고,,,, 그 아이디들에 해당하는 피드 개수 구하기
        List<ChallengeMember> challengeMembers = challengeMemberRepository.findAllByMemberId(memberId);

        List<Long> challengeMemberIds = new ArrayList<>();

        for(ChallengeMember c : challengeMembers){
            challengeMemberIds.add(c.getId());
        }

        Long feedNum = feedRepository.countByChallengeMember_IdIn(challengeMemberIds);

        // 달성한 챌린지 : achievement 테이블에서 개수 가져오기,,, 카테고리로 필터링 안하고 그냥 회원id 같은거 모두 카운트
        List<Achievement> achievements = achievementRepository.findByMember_Id(memberId);
        Integer totalAchievements = 0;

        for(Achievement a : achievements){
            totalAchievements+=a.getCount();
        }

        // 다짐 : 회원 테이블에서 가져오기
        String resolution = member.getResolution();

        // 챌린지 개수 : 멤버 아이디로 챌린지멤버 테이블 카운트
        Integer challengeNum = challengeMemberRepository.countByMember_Id(memberId).intValue();

        // 실패한 챌린지 : ,, 이건 우짜지?
        Integer fail = challengeNum- totalAchievements;

        // 찜 개수 : 회원 아이디로 챌린지라이크 테이블 접근해서 카운트
        Long likes = feedLikeRepository.countByMember_Id(memberId);

        // 현재 보유 포인트 : 회원 테이블에서 가져오기
        Integer points = member.getPoint();

        // 피드,, 는 찾기,,,, : 최근 4개 피드만 반환
        List<Feed> feeds = feedRepository.findTop4ByChallengeMember_IdInOrderByCreatedAtDesc(challengeMemberIds);
        FeedDTO.previewFeedListDto feedDto = feedConverter.toPreviewFeedListDto(feeds);

        String nickName = member.getNickName();
        String profileImage = member.getProfileUrl();

        return MemberResponseDTO.myPageDTO.builder()
                .achieve(totalAchievements)
                .likes(likes)
                .points(points)
                .feedNum(feedNum)
                .resolution(resolution)
                .challenges(challengeNum)
                .fail(fail)
                .feedDTOs(feedDto)
                .nickName(nickName)
                .profileImage(profileImage)
                .build();
    }

    /***
     * 마이페이지에서 포인트 로그 조회
     * @return
     */
    public PointResponseDTO.pointLogListDTO getPointLog(Long memberId){

        Member member = memberRepository.findById(memberId).orElseThrow(()->{throw new IllegalArgumentException("멤버없다");});

        List<PointLog> pointLogs = pointLogRepository.findAllByMember_Id(memberId);

        PointResponseDTO.pointLogListDTO pointTotalLogs = PointConverter.pointLogListDTO(pointLogs,member);

        return pointTotalLogs;
    }

    /***
     * 프로필 수정
     * @param memberUpdateDto
     * @return
     */
    public MemberResponseDTO.profileDTO updateProfile(MemberUpdateDto memberUpdateDto,Long memberId){

        Member member = memberRepository.findById(memberId).get();


        member.update(memberUpdateDto);


        return MemberConverter.toProfileDTO(member);
    }

    public MemberResponseDTO.ProfileImageDTO updateProfileImage(MemberUpdateDto.ProfileImageDTO profileImageDTO, Long memberId){

        Member member = memberRepository.findById(memberId).get();

        Uuid feedUuid = uuidRepository.save(Uuid.builder().uuid(UUID.randomUUID().toString()).build());
        String feedUrl = s3Manager.uploadFile(s3Manager.generateFeedKeyName(feedUuid), profileImageDTO.getProfileImage());

        member.updateProfileImageUrl(feedUrl);

        return MemberResponseDTO.ProfileImageDTO.builder()
                .memberId(memberId)
                .imgageUrl(feedUrl)
                .build();
    }

    public boolean checkNickName(String nickName){

        boolean isExists = memberRepository.existsByNickName(nickName);

        if (isExists){ // 중복된 회원이 있을 경우
            return false;
        }
        else{
            return true;
        }
    }

    /***
     * 멤버의 챌린지 참여 여부 판단
     * @param memberId 멤버
     * @param challengeId 챌린지
     * @return CheckEnrolledDTO 멤버가 해당 챌린지에 참여 중인지와, 참여 중인 챌린지의 id 값들을 보내준다
     */
    public MemberResponseDTO.CheckEnrolledDTO checkEnroll(Long memberId, Long challengeId) {

        List<Long> enrolledChallengeIds = challengeMemberRepository.findAllByMemberId(memberId).stream()
                .filter(challengeMember -> challengeMember.getChallenge().getFinishDate().isAfter(LocalDate.now())) // 아직 끝나지 않은 챌린지만
                .map(challengeMember -> challengeMember.getChallenge().getId())
                .toList();

//        boolean hasDuplicates = enrolledChallengeIds.stream().distinct().count() != enrolledChallengeIds.size();
//        if (hasDuplicates) throw new ErrorHandler(ErrorStatus.HAS_DUPLICATE_CHALLENGE);

        Boolean isEnrolled = enrolledChallengeIds.contains(challengeId);

        return MemberConverter.toCheckEnrolledDTO(isEnrolled, enrolledChallengeIds);
    }
}