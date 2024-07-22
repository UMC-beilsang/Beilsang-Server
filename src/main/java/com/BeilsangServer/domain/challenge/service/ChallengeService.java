package com.BeilsangServer.domain.challenge.service;

import com.BeilsangServer.aws.s3.AmazonS3Manager;
import com.BeilsangServer.domain.achievment.entity.Achievement;
import com.BeilsangServer.domain.achievment.repository.AchievementRepository;
import com.BeilsangServer.domain.challenge.converter.ChallengeConverter;
import com.BeilsangServer.domain.challenge.dto.ChallengeRequestDTO;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.entity.ChallengeNote;
import com.BeilsangServer.domain.challenge.repository.ChallengeNoteRepository;
import com.BeilsangServer.domain.challenge.repository.ChallengeRepository;
import com.BeilsangServer.domain.like.entity.ChallengeLike;
import com.BeilsangServer.domain.like.repository.ChallengeLikeRepository;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.ChallengeMemberRepository;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.BeilsangServer.domain.point.entity.PointLog;
import com.BeilsangServer.domain.point.repository.PointLogRepository;
import com.BeilsangServer.domain.uuid.entity.Uuid;
import com.BeilsangServer.domain.uuid.repository.UuidRepository;
import com.BeilsangServer.global.common.apiPayload.code.status.ErrorStatus;
import com.BeilsangServer.global.common.exception.handler.ErrorHandler;
import com.BeilsangServer.global.enums.Category;
import com.BeilsangServer.global.enums.ChallengeStatus;
import com.BeilsangServer.global.enums.PointName;
import com.BeilsangServer.global.enums.PointStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeNoteRepository challengeNoteRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeLikeRepository challengeLikeRepository;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final AchievementRepository achievementRepository;
    private final MemberRepository memberRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;
    private final PointLogRepository pointLogRepository;

    /***
     * 챌린지 생성하기
     * @param request 챌린지 생성에 필요한 정보
     * @return CreateDTO
     * 이미지 업로드
     */
    @Transactional
    public ChallengeResponseDTO.ChallengePreviewDTO createChallenge(ChallengeRequestDTO.CreateChallengeDTO request, Long memberId) {

        // 이미지 업로드
        Uuid mainUuid = uuidRepository.save(Uuid.builder().uuid(UUID.randomUUID().toString()).build());
        String mainImageUrl = s3Manager.uploadFile(s3Manager.generateMainKeyName(mainUuid), request.getMainImage());
        Uuid certUuid = uuidRepository.save(Uuid.builder().uuid(UUID.randomUUID().toString()).build());
        String certImageUrl = s3Manager.uploadFile(s3Manager.generateCertKeyName(certUuid), request.getCertImage());

        // 컨버터를 사용해 DTO를 챌린지 엔티티로 변환
        Challenge challenge = ChallengeConverter.toChallenge(request, mainImageUrl, certImageUrl);
        challengeRepository.save(challenge);

        // 멤버 포인트 차감, 포인트 부족할 시 예외처리
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        int memberPoint = member.getPoint();
        if (challenge.getJoinPoint() > memberPoint) throw new ErrorHandler(ErrorStatus.POINT_LACK); // 예외처리
        member.subPoint(challenge.getJoinPoint()); // 포인트 차감

        // 포인트 기록 생성 및 디비 저장
        pointLogRepository.save(PointLog.builder()
                .pointName(PointName.JOIN_CHALLENGE)
                .status(PointStatus.USE)
                .value(challenge.getJoinPoint())
                .member(member)
                .build());

        // 리스트로 받은 리스트 데이터를 ChallengeNote 엔티티 각각에 담고 저장
        List<String> notes = request.getNotes();
        notes.stream()
                .map(note -> ChallengeNote.builder().note(note).challenge(challenge).build())
                .forEach(challengeNoteRepository::save);

        // ChallengeMember, Challenge 저장
        challengeMemberRepository.save(
                ChallengeMember.builder()
                        .challenge(challenge)
                        .member(member)
                        .isHost(true)
                        .successDays(0)
                        .challengeStatus(ChallengeStatus.NOT_YET)
                        .isFeedUpload(false)
                        .build());

        return ChallengeConverter.toChallengePreviewDTO(challenge, member.getNickName());
    }

    /***
     * 해당하는 챌린지 세부 내용 조회하기
     * @param challengeId 챌린지 ID
     * @return ChallengeDTO
     */
    public ChallengeResponseDTO.ChallengeDTO getChallenge(Long challengeId, Long memberId) {

        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new ErrorHandler(ErrorStatus.CHALLENGE_NOT_FOUND));

        // 챌린지 시작 D-day 계산
        Integer dDay = (int) LocalDate.now().until(challenge.getStartDate(), ChronoUnit.DAYS);

        // 찜 여부
        Boolean like = challengeLikeRepository.existsByChallengeIdAndMemberId(challengeId, memberId);

        // 챌린지 호스트 이름 찾기
        String hostName = getHostName(challengeId);

        return ChallengeConverter.toChallengeDTO(challenge, dDay, hostName, like);
    }

    /***
     * 추천 챌린지 미리보기 조회하기
     * @return ChallengeResponseDTO.RecommendChallengeDTO 2개를 리스트로 반환
     * 이미 참여한 챌린지는 추천하지 않도록 추가 구현 필요
     */
    public List<ChallengeResponseDTO.RecommendChallengeDTO> getRecommendChallenges(Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

//        Category keyword = Category.from(member.getKeyword());
        Category keyword = member.getKeyword();

        // 이미 참여한 챌린지는 추천하지 않도록 추가 구현
//        List<Long> enrolledChallengeIds = challengeMemberRepository.findAllByMember_id(memberId).stream()
//                .filter(challengeMember -> challengeMember.getChallenge().getFinishDate().isAfter(LocalDate.now())) // 아직 끝나지 않은 챌린지만
//                .map(challengeMember -> challengeMember.getChallenge().getId())
//                .toList();
//        List<Challenge> recommendChallenges = challengeRepository.findAllByStartDateAfterOrderByCountLikesDesc(LocalDate.now());
//        List<Challenge> challenges = recommendChallenges
//                .stream().filter(challenge -> keyword.equals(challenge.getCategory()) && !enrolledChallengeIds.contains(challenge.getId()))
//                .limit(2)
//                .toList();

        int recommendNum = 2;
        List<Challenge> recommendChallenges = challengeRepository.findAllByStartDateAfterOrderByCountLikesDesc(LocalDate.now())
                .stream().filter(challenge -> keyword.equals(challenge.getCategory()))
                .limit(recommendNum)
                .toList();

        //if (recommendChallenges.size() < recommendNum) throw new ErrorHandler(ErrorStatus.CHALLENGE_INSUFFICIENT);

        return recommendChallenges.stream()
                .map(challenge -> ChallengeResponseDTO.RecommendChallengeDTO.builder()
                        .challengeId(challenge.getId())
                        .imageUrl(challenge.getMainImageUrl())
                        .title(challenge.getTitle())
                        .category(challenge.getCategory())
                        .attendeeCount(challenge.getAttendeeCount())
                        .build())
                .collect(Collectors.toList());
    }

    /***
     * 해당하는 카테고리의 챌린지 목록 조회하기
     * @param stringCategory 문자열 타입의 Category
     * @return ChallengePreviewListDTO
     * 시작된 챌린지는 뺄지, 정렬 순서 어떻게 할지 등 논의 필요
     */
    public ChallengeResponseDTO.ChallengePreviewListDTO getChallengePreviewList(String stringCategory) {

        Category category = Category.from(stringCategory);
        List<Challenge> challenges = challengeRepository.findAllByStartDateAfterAndCategoryOrderByAttendeeCountDesc(LocalDate.now(), category);

        List<ChallengeResponseDTO.ChallengePreviewDTO> challengePreviewDTOList = challenges.stream()
                .map(challenge -> ChallengeConverter.toChallengePreviewDTO(challenge, getHostName(challenge.getId())))
                .toList();

        return ChallengeResponseDTO.ChallengePreviewListDTO.builder().challenges(challengePreviewDTOList).build();
    }

    /***
     * 전체 챌린지 목록 조회하기
     * @return ChallengePreviewListDTO
     */
    public ChallengeResponseDTO.ChallengePreviewListDTO getChallengePreviewList() {

        List<Challenge> challenges = challengeRepository.findAllByStartDateAfterOrderByAttendeeCountDesc(LocalDate.now());

        List<ChallengeResponseDTO.ChallengePreviewDTO> challengePreviewDTOList = challenges.stream()
                .map(challenge -> ChallengeConverter.toChallengePreviewDTO(challenge, getHostName(challenge.getId())))
                .toList();

        return ChallengeResponseDTO.ChallengePreviewListDTO.builder().challenges(challengePreviewDTOList).build();
    }

    /***
     * 챌린지 목록 제한된 갯수로 조회하기
     * @return ChallengePreviewListDTO
     */
    public ChallengeResponseDTO.ChallengePreviewListDTO getLimitedChallengePreviewList() {

        // 보여줄 챌린지의 갯수 설정
        int limitNum = 2;

        List<Challenge> challenges = challengeRepository.findAllByStartDateAfterOrderByAttendeeCountDesc(LocalDate.now());

        List<ChallengeResponseDTO.ChallengePreviewDTO> challengePreviewDTOList = challenges.stream()
                .map(challenge -> ChallengeConverter.toChallengePreviewDTO(challenge, getHostName(challenge.getId())))
                .limit(limitNum)
                .toList();

        return ChallengeResponseDTO.ChallengePreviewListDTO.builder().challenges(challengePreviewDTOList).build();
    }

    /***
     * 명예의 전당 조회 (카테고리별 찜수 기준 상위 10개 챌린지)
     * @param category
     * @return 상의 10개 챌린지를 담은 challengePreviewListDto
     */
    public ChallengeResponseDTO.ChallengePreviewListDTO getFamousChallengeList(String category) {

        Category categoryByEnum = Category.from(category);
        List<Challenge> challenges = challengeRepository.findTop5ByCategoryOrderByCountLikesDesc(categoryByEnum);

        List<ChallengeResponseDTO.ChallengePreviewDTO> challengePreviewDTOList = challenges.stream()
                .map(challenge -> ChallengeConverter.toChallengePreviewDTO(challenge, getHostName(challenge.getId())))
                .toList();

        return ChallengeResponseDTO.ChallengePreviewListDTO.builder().challenges(challengePreviewDTOList).build();
    }

    /***
     * 회원 별 찜 목록 조회
     * @param memberId
     * @return 찜 목록을 담은 challengePreviewListDto
     */
    public ChallengeResponseDTO.ChallengePreviewListDTO getLikesList(Long memberId, String category) {
        Category categoryByEnum = Category.from(category);

        List<ChallengeLike> challengeLikes = challengeLikeRepository.findAllByMember_Id(memberId); // ChallengeLike 테이블에서 memberId 와 관련된 challengeId 추출

        List<Long> challengeIds = new ArrayList<>();
        for (ChallengeLike c : challengeLikes) {
            challengeIds.add(c.getChallenge().getId());
        }


        // 챌린지 찾기
        //List<Challenge> challenges = challengeRepository.findAllById(challengeIds);
        List<Challenge> challenges;
        if (categoryByEnum.equals(Category.ALL)) {
            challenges = challengeRepository.findAllById(challengeIds);
        } else {
            challenges = challengeRepository.findAllByIdInAndCategory(challengeIds, categoryByEnum);
        }

        List<ChallengeResponseDTO.ChallengePreviewDTO> challengePreviewDTOList = challenges.stream()
                .map(challenge -> ChallengeConverter.toChallengePreviewDTO(challenge, getHostName(challenge.getId())))
                .toList();

        return ChallengeResponseDTO.ChallengePreviewListDTO.builder().challenges(challengePreviewDTOList).build();
    }

    /***
     * 카테고리와 상태로 나의 챌린지 조회
     * @param status
     * @param category
     * @param memberId
     * @return
     */
    public ChallengeResponseDTO.ChallengeListWithCountDTO getChallengeByStatusAndCategory(String status, String category, Long memberId) {
        Category categoryByEnum = Category.from(category);

        List<ChallengeMember> challengeMembers = challengeMemberRepository.findAllByMemberId(memberId);

        List<Achievement> achievements = achievementRepository.findAllByMember_Id(memberId);

        int count = 0;

        for (Achievement a : achievements) {
            if (categoryByEnum.equals(a.getCategory())) {
                count = a.getCount();
            }
        }

        LocalDate now = LocalDate.now();

        List<Long> challengeIds = new ArrayList<>();

        for (ChallengeMember c : challengeMembers) {
            if ("참여중".equals(status) && isAfterOrEqual(c.getChallenge().getFinishDate(), now) &&
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

        List<Challenge> challenges = challengeRepository.findAllById(challengeIds);

        List<ChallengeResponseDTO.ChallengePreviewDTO> challengePreviewDTOList = challenges.stream()
                .map(challenge -> ChallengeConverter.toChallengePreviewDTO(challenge, getHostName(challenge.getId())))
                .toList();

        return ChallengeResponseDTO.ChallengeListWithCountDTO.builder()
                .challenges(ChallengeResponseDTO.ChallengePreviewListDTO.builder().challenges(challengePreviewDTOList).build())
                .count(count)
                .build();
    }

    /***
     * 챌린지 참여하기
     * @param challengeId 참여하는 챌린지 ID
     * @param memberId 참여하는 멤버 ID
     * @return JoinChallengeDTO
     */
    @Transactional
    public ChallengeResponseDTO.JoinChallengeDTO joinChallenge(Long challengeId, Long memberId) {

        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new ErrorHandler(ErrorStatus.CHALLENGE_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 멤버 포인트 차감, 포인트 부족할 시 예외처리
        int memberPoint = member.getPoint();
        if (challenge.getJoinPoint() > memberPoint) throw new ErrorHandler(ErrorStatus.POINT_LACK); // 예외처리
        member.subPoint(challenge.getJoinPoint()); // 포인트 차감

        // 멤버가 이미 참여한 첼린지인지 확인 후 참여되어 있다면 예외처리
        if (challengeMemberRepository.findByMember_idAndChallenge_Id(memberId, challengeId).isPresent()) {
            throw new ErrorHandler(ErrorStatus.JOIN_DUPLICATE_CHALLENGE);
        }


        // 포인트 기록 생성 및 디비 저장
        pointLogRepository.save(PointLog.builder()
                .pointName(PointName.JOIN_CHALLENGE)
                .status(PointStatus.USE)
                .value(challenge.getJoinPoint())
                .member(member)
                .build());

        // 참여인원, 모인 포인트 증가
        challenge.increaseAttendeeCount();
        challenge.addCollectedPoint(challenge.getJoinPoint());
        challengeRepository.save(challenge);

        challengeMemberRepository.save(
                ChallengeMember.builder()
                        .challenge(challenge)
                        .member(member)
                        .isHost(false)
                        .successDays(0)
                        .challengeStatus(ChallengeStatus.NOT_YET)
                        .isFeedUpload(false)
                        .build()
        );

        // 챌린지 호스트 찾기
        String hostName = getHostName(challengeId);

        return ChallengeConverter.toJoinChallengeDTO(member, challenge, hostName);
    }

    /***
     * 챌린지 찜하기
     * @param challengeId
     * @return
     * 멤버 추가 필요
     */
    @Transactional
    public Long challengeLike(Long challengeId, Long memberId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHALLENGE_NOT_FOUND));

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        ChallengeLike challengeLike = ChallengeLike.builder()
                .challenge(challenge)
                .member(member)
                .build();

        challengeLikeRepository.save(challengeLike);
        challenge.increaseCountLikes();

        return challengeLike.getId();
    }

    /***
     * 챌린지 찜 취소하기
     * @param challengeId
     * @return
     */
    @Transactional
    public Long challengeUnLike(Long challengeId, Long memberId) {

        ChallengeLike challengeLike = challengeLikeRepository.findByChallengeIdAndMemberId(challengeId, memberId);
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new ErrorHandler(ErrorStatus.CHALLENGE_NOT_FOUND));

        challenge.decreaseCountLikes();
        challengeLikeRepository.delete(challengeLike);

        return challengeLike.getId();
    }

    /***
     * 챌린지 호스트 이름 찾기
     * @param challengeId 찾으려는 챌린지 ID
     * @return 호스트 이름
     */
    public String getHostName(Long challengeId) {

        Member host = challengeMemberRepository.findByChallengeIdAndIsHostIsTrue(challengeId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.CHALLENGE_HOST_NOT_FOUND))
                .getMember();

        return host.getNickName();
    }

    public static boolean isAfterOrEqual(LocalDate date1, LocalDate date2) {
        return !date1.isBefore(date2);
    }


    /***
     * 참여중인 챌린지 조회
     * @param memberId 로그인된 멤버
     * @return MyChallengePreviewListDTO
     * 멤버가 참여중인 챌린지의 달성률을 계산하여 지정한 갯수 만큼 리스트 형태로 반환한다.
     */
    public ChallengeResponseDTO.MyChallengePreviewListDTO getMyChallengePreviewList(Long memberId) {

        int limit = 2;

        // 멤버의 참여중인 챌린지
        List<ChallengeResponseDTO.MyChallengePreviewDTO> myChallengePreviewDTOList =
                challengeMemberRepository.findAllByMemberId(memberId)
                        .stream()
                        .filter(challengeMember ->
                                challengeMember.getChallengeStatus() == ChallengeStatus.ONGOING ||
                                        challengeMember.getChallengeStatus() == ChallengeStatus.NOT_YET)
                        .map(challengeMember -> {
                                    Challenge challenge = challengeMember.getChallenge();
                                    float achieveRate = (float) challengeMember.getSuccessDays() / challenge.getTotalGoalDay() * 100;
                                    return ChallengeConverter.toMyChallengePreviewDTO(challenge, achieveRate);
                                }
                        )
                        .limit(limit)
                        .toList();

        return ChallengeResponseDTO.MyChallengePreviewListDTO.builder()
                .challenges(myChallengePreviewDTOList)
                .build();
    }

}
