package com.BeilsangServer.domain.challenge.service;

import com.BeilsangServer.domain.achievment.entity.Achievement;
import com.BeilsangServer.domain.achievment.repository.AchievementRepository;
import com.BeilsangServer.domain.challenge.converter.ChallengeConverter;
import com.BeilsangServer.domain.challenge.dto.ChallengeRequestDTO;
import com.BeilsangServer.domain.challenge.dto.ChallengeResponseDTO;
import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.entity.ChallengeNote;
import com.BeilsangServer.domain.challenge.repository.ChallengeNoteRepository;
import com.BeilsangServer.domain.challenge.repository.ChallengeRepository;
import com.BeilsangServer.domain.feed.entity.FeedLike;
import com.BeilsangServer.domain.like.entity.ChallengeLike;
import com.BeilsangServer.domain.like.repository.ChallengeLikeRepository;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.domain.member.repository.ChallengeMemberRepository;
import com.BeilsangServer.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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

    /***
     * 챌린지 생성하기
     * @param request 챌린지 생성에 필요한 정보
     * @return CreateDTO
     * 이미지 업로드, 호스트 추가 필요
     */
    @Transactional
    public Challenge createChallenge(ChallengeRequestDTO.CreateDTO request) {

        // 컨버터를 사용해 DTO를 챌린지 엔티티로 변환
        Challenge challenge = ChallengeConverter.toChallenge(request);

        // 리스트로 받은 리스트 데이터를 반복문을 통해 ChallengeNote 엔티티 각각에 담고 저장
        List<String> notes = request.getNotes();
        for (String note : notes) {
            ChallengeNote challengeNote = ChallengeNote.builder().note(note).challenge(challenge).build();
            challengeNoteRepository.save(challengeNote);
        }

        return challengeRepository.save(challenge);
    }

    /***
     * 해당하는 챌린지 세부 내용 조회하기
     * @param challengeId 챌린지 ID
     * @return ChallengeDTO
     */
    public ChallengeResponseDTO.ChallengeDTO getChallenge(Long challengeId) {

        Challenge challenge = challengeRepository.findById(challengeId).get();

        Integer dDay = (int) LocalDate.now().until(challenge.getStartDate(), ChronoUnit.DAYS);

        return ChallengeConverter.toChallengeDTO(challenge, dDay, getRecommendChallenges());
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
                        .imageUrl(challenge.getImageUrl())
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
        List<Challenge> challenges = challengeRepository.findAllByCategory(category);
        return ChallengeConverter.toChallengePreviewListDTO(challenges);
    }

    /***
     * 전체 챌린지 목록 조회하기
     * @return ChallengePreviewListDTO
     * 시작된 챌린지는 뺄지, 정렬 순서 어떻게 할지 등 논의 필요
     */
    public ChallengeResponseDTO.ChallengePreviewListDTO getChallengePreviewList() {

        List<Challenge> challenges = challengeRepository.findAll();
        return ChallengeConverter.toChallengePreviewListDTO(challenges);
    }


    /***
     * 명예의 전당 조회 (카테고리별 찜수 기준 상위 10개 챌린지)
     * @param category
     * @return 상의 10개 챌린지를 담은 challengePreviewListDto
     */
    public ChallengeResponseDTO.ChallengePreviewListDTO getFamousChallengeList(String category){

        Category categoryByEnum = Category.valueOf(category);
        List<Challenge> challenges = challengeRepository.findTop10ByCategoryOrderByCountLikesDesc(categoryByEnum);

        return ChallengeConverter.toChallengePreviewListDTO(challenges);
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
        List<Challenge> challengeList = challengeRepository.findAllById(challengeIds);

        return ChallengeConverter.toChallengePreviewListDTO(challengeList);
    }

    public ChallengeResponseDTO.ChallengeListWithCountDTO getChallengeByStatusAndCategory(String status, String category, Long memberId){
        Category categoryByEnum = Category.valueOf(category);

        List<ChallengeMember> challengeMembers = challengeMemberRepository.findAllByMember_id(memberId);

        List<Achievement> achievements = achievementRepository.findAllByMember_Id(memberId);

        Integer count = 0;

        for (Achievement a : achievements){
            if(categoryByEnum.equals(a.getCategory())){
                count = a.getCount();
            }
        }

        LocalDate now = LocalDate.now();

        List<Long> challengeIds = new ArrayList<>();

        for (ChallengeMember c : challengeMembers) {
            if ("참여중".equals(status) && c.getChallenge().getFinishDate().isAfter(now) && categoryByEnum.equals(c.getChallenge().getCategory())) {
                challengeIds.add(c.getChallenge().getId());
            } else if ("등록한".equals(status) && c.getIsHost() && categoryByEnum.equals(c.getChallenge().getCategory())) {
                challengeIds.add(c.getChallenge().getId());
            } else if ("완료된".equals(status) && c.getChallenge().getFinishDate().isBefore(now) && categoryByEnum.equals(c.getChallenge().getCategory())) {
                challengeIds.add(c.getChallenge().getId());
            }
        }

        List<Challenge> challengeList = challengeRepository.findAllById(challengeIds);

        ChallengeResponseDTO.ChallengePreviewListDTO challengePreviewList = ChallengeConverter.toChallengePreviewListDTO(challengeList);

        ChallengeResponseDTO.ChallengeListWithCountDTO challengeListWithCount = ChallengeResponseDTO.ChallengeListWithCountDTO.builder()
                .challenges(challengePreviewList)
                .count(count)
                .build();

        return challengeListWithCount;
    }
}
