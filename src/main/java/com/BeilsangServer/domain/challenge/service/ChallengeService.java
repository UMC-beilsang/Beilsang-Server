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
import com.BeilsangServer.domain.uuid.entity.Uuid;
import com.BeilsangServer.domain.uuid.repository.UuidRepository;
import com.BeilsangServer.global.enums.Category;
import com.BeilsangServer.global.enums.ChallengeStatus;
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

    /***
     * 챌린지 생성하기
     * @param request 챌린지 생성에 필요한 정보
     * @return CreateDTO
     * 이미지 업로드
     */
    @Transactional
    public ChallengeResponseDTO.ChallengePreviewDTO createChallenge(ChallengeRequestDTO.CreateChallengeDTO request, Long memberId) {

        Member member = memberRepository.findById(memberId).get();

        // 이미지 업로드
        Uuid mainUuid = uuidRepository.save(Uuid.builder().uuid(UUID.randomUUID().toString()).build());
        String mainImageUrl = s3Manager.uploadFile(s3Manager.generateMainKeyName(mainUuid), request.getMainImage());
        Uuid certUuid = uuidRepository.save(Uuid.builder().uuid(UUID.randomUUID().toString()).build());
        String certImageUrl = s3Manager.uploadFile(s3Manager.generateCertKeyName(certUuid), request.getCertImage());

        // 컨버터를 사용해 DTO를 챌린지 엔티티로 변환
        Challenge challenge = ChallengeConverter.toChallenge(request, mainImageUrl, certImageUrl);

        // 리스트로 받은 리스트 데이터를 반복문을 통해 ChallengeNote 엔티티 각각에 담고 저장
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
        challengeRepository.save(challenge);

        return ChallengeConverter.toChallengePreviewDTO(challenge, member.getNickName());
    }

    /***
     * 해당하는 챌린지 세부 내용 조회하기
     * @param challengeId 챌린지 ID
     * @return ChallengeDTO
     */
    public ChallengeResponseDTO.ChallengeDTO getChallenge(Long challengeId) {

        Challenge challenge = challengeRepository.findById(challengeId).get();

        // 챌린지 시작 D-day 계산
        Integer dDay = (int) LocalDate.now().until(challenge.getStartDate(), ChronoUnit.DAYS);

        // 챌린지 호스트 이름 찾기
        String hostName = challengeMemberRepository.findByChallenge_IdAndIsHostIsTrue(challengeId).getMember().getNickName();

        return ChallengeConverter.toChallengeDTO(challenge, dDay, hostName);
    }

    /***
     * 추천 챌린지 미리보기 조회하기
     * @return ChallengeResponseDTO.RecommendChallengeDTO 2개를 리스트로 반환
     */
    public List<ChallengeResponseDTO.RecommendChallengeDTO> getRecommendChallenges() {

        // JPA를 사용해 아직 시작 안한 챌린지 중 좋아요 많은 2개를 리스트로 만들어 반환한다
        List<Challenge> recommendChallenges = challengeRepository.findTop2ByStartDateAfterOrderByCountLikesDesc(LocalDate.now());

        return recommendChallenges.stream()
                .map(challenge -> ChallengeResponseDTO.RecommendChallengeDTO.builder()
                        .challengeId(challenge.getId())
                        .imageUrl(challenge.getMainImageUrl())
                        .title(challenge.getTitle())
                        .category(challenge.getCategory())
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
     * 시작된 챌린지는 뺄지, 정렬 순서 어떻게 할지 등 논의 필요
     */
    public ChallengeResponseDTO.ChallengePreviewListDTO getChallengePreviewList() {

        List<Challenge> challenges = challengeRepository.findAllByStartDateAfterOrderByAttendeeCountDesc(LocalDate.now());

        List<ChallengeResponseDTO.ChallengePreviewDTO> challengePreviewDTOList = challenges.stream()
                .map(challenge -> ChallengeConverter.toChallengePreviewDTO(challenge, getHostName(challenge.getId())))
                .toList();

        return ChallengeResponseDTO.ChallengePreviewListDTO.builder().challenges(challengePreviewDTOList).build();
    }


    /***
     * 명예의 전당 조회 (카테고리별 찜수 기준 상위 10개 챌린지)
     * @param category
     * @return 상의 10개 챌린지를 담은 challengePreviewListDto
     */
    public ChallengeResponseDTO.ChallengePreviewListDTO getFamousChallengeList(String category){

        Category categoryByEnum = Category.valueOf(category);
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
    public ChallengeResponseDTO.ChallengePreviewListDTO getLikesList(Long memberId) {
        List<ChallengeLike> challengeLikes = challengeLikeRepository.findAllByMember_Id(memberId); // ChallengeLike 테이블에서 memberId 와 관련된 challengeId 추출

        List<Long> challengeIds = new ArrayList<>();
        for (ChallengeLike c : challengeLikes) {
            challengeIds.add(c.getChallenge().getId());
        }

        // 챌린지 찾기
        List<Challenge> challenges = challengeRepository.findAllById(challengeIds);

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
    public ChallengeResponseDTO.ChallengeListWithCountDTO getChallengeByStatusAndCategory(String status, String category, Long memberId){
        Category categoryByEnum = Category.valueOf(category);

        List<ChallengeMember> challengeMembers = challengeMemberRepository.findAllByMember_id(memberId);

        List<Achievement> achievements = achievementRepository.findAllByMember_Id(memberId);

        int count = 0;

        for (Achievement a : achievements){
            if(categoryByEnum.equals(a.getCategory())){
                count = a.getCount();
            }
        }

        LocalDate now = LocalDate.now();

        List<Long> challengeIds = new ArrayList<>();

        for (ChallengeMember c : challengeMembers) {
            if ("참여중".equals(status) && c.getChallenge().getFinishDate().isAfter(now) &&
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

        Member member = memberRepository.findById(memberId).get();

        Challenge challenge = challengeRepository.findById(challengeId).get();
        challenge.increaseAttendeeCount();
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
    public Long challengeLike(Long challengeId, Long memberId){
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(()->{throw new IllegalArgumentException("챌린지없다.");});

        Member member = memberRepository.findById(memberId).orElseThrow(()->{throw new IllegalArgumentException("멤버없다");});
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
    public Long challengeUnLike(Long challengeId, Long memberId){
        ChallengeLike challengeLike = challengeLikeRepository.findByChallenge_IdAndMember_Id(challengeId,memberId);
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(()->{throw new IllegalArgumentException("없다");});

        challenge.decreaseCountLikes();;
        challengeLikeRepository.delete(challengeLike);

        return challengeLike.getId();
    }

    /***
     * 챌린지 호스트 이름 찾기
     * @param challengeId 찾으려는 챌린지 ID
     * @return 호스트 이름
     */
    public String getHostName(Long challengeId) {

        Member host = challengeMemberRepository.findByChallenge_IdAndIsHostIsTrue(challengeId).getMember();
        return host.getNickName();
    }

}
