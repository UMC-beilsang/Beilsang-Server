package com.BeilsangServer.domain.member.service;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.repository.ChallengeRepository;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.ChallengeMemberRepository;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.BeilsangServer.global.enums.ChallengeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeMemberService {

    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;

    /***
     * 스케줄러를 사용하여 매일 00시 정각마다 작업을 수행
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void dailyTasks() {
        checkFailure();
        checkSuccess();
        makeIsFeedUploadFalse();
        checkIsChallengeStart();
    }

    /***
     * 하루동안 인증하지 않은 챌린지 멤버의 상태 수정
     * 피드가 올라가 있지 않은 챌린지 멤버의 상태 변경
     */
    @Transactional
    public void checkFailure() {

        List<ChallengeMember> notUploadedMember = challengeMemberRepository.findAllByChallengeStatusAndIsFeedUpload(ChallengeStatus.ONGOING, false);

        for (ChallengeMember challengeMember : notUploadedMember) {

            Challenge challenge = challengeMember.getChallenge();

            // 지금까지 성공한 일수
            int remainSuccess = challenge.getTotalGoalDay() - challengeMember.getSuccessDays();
            // 챌린지 종료일까지 남은 일수
            int remainDay = LocalDate.now().until(challenge.getFinishDate()).getDays() + 1;

            // 남은 일수 확인 후 목표 일수를 다 채우면 성공, 남은 기간 내에 다 채우지 못하면 실패로 변경
            if (remainSuccess == 0) {
                challengeMember.updateChallengeStatus(ChallengeStatus.SUCCESS);
                challengeMemberRepository.save(challengeMember);
            } else if (remainSuccess > remainDay) {
                challengeMember.updateChallengeStatus(ChallengeStatus.FAIL);
                challengeMemberRepository.save(challengeMember);
            }
        }
    }

    /***
     * 모든 챌린지 멤버의 당일 인증 상태 수정
     * 아직 끝나지 않은 챌린지만 대상이 되어야 함
     */
    @Transactional
    public void makeIsFeedUploadFalse() {

        // 매일 정시마다 모든 챌린지 멤버의 인증 상태 수정
        challengeMemberRepository.findAllByChallengeStatus(ChallengeStatus.ONGOING).forEach(challengeMember -> {
            challengeMember.makeIsFeedUploadFalse();
            challengeMemberRepository.save(challengeMember);
        });
    }

    /***
     * 시작하지 않은 챌린지의 상태를 진행 중으로 변경
     */
    @Transactional
    public void checkIsChallengeStart() {

        List<ChallengeMember> notYetChallengeMembers = challengeMemberRepository.findAllByChallengeStatus(ChallengeStatus.NOT_YET);
        notYetChallengeMembers.stream()
                .filter(challengeMember -> challengeMember.getChallenge().getStartDate().equals(LocalDate.now()))
                .forEach(challengeMember -> {
                    challengeMember.updateChallengeStatus(ChallengeStatus.ONGOING);
                    challengeMemberRepository.save(challengeMember);
                });
    }

    /***
     * 피드 생성 시 챌린지 인증 처리
     * @param challengeMember 피드를 생성한 주체
     */
    @Transactional
    public void checkFeedUpload(ChallengeMember challengeMember) {

        // 처음 피드가 올라가는 경우에만 isFeedUpload, successDays 수정
        if (!challengeMember.getIsFeedUpload()) {
            challengeMember.makeIsFeedUploadTrue();
            challengeMember.increaseSuccessDays();
            challengeMemberRepository.save(challengeMember);
        }
    }

    /***
     * 성공한 챌린지의 수익 배분
     * 호스트는 참가비의 2배를 돌려 받고, 일반 참여자는 실패한 사람들의 포인트를 나눈다.
     * 이때 100원 단위로 돌려주기 위해 올림 처리를 하여 지급한다.
     */
    @Transactional
    public void checkSuccess() {

        List<Challenge> finishedChallenges = challengeRepository.findAllByFinishDate(LocalDate.now().minusDays(1));

        for (Challenge challenge : finishedChallenges) {

            List<ChallengeMember> successMembers = challengeMemberRepository.findAllByChallengeId(challenge.getId())
                    .stream()
                    .filter(challengeMember -> challengeMember.getChallengeStatus().equals(ChallengeStatus.SUCCESS))
                    .toList();

            // 성공한 멤버가 없는 경우 예외 발생
            if (successMembers.isEmpty()) throw new RuntimeException("성공한 사람이 없습니다.");

            int pointDivide = challenge.getCollectedPoint() / successMembers.size();
            int liftedPoint = pointDivide + (100 - pointDivide % 100); // 100원 단위로 돌려주기 위해 올림

            // 성공한 멤버 중 호스트의 경우 2배로 지급
            successMembers
                    .forEach(challengeMember -> {
                        Member member = challengeMember.getMember();
                        int pointsToAdd = challengeMember.getIsHost() ? liftedPoint * 2 : liftedPoint;
                        member.addPoint(pointsToAdd);
                        memberRepository.save(member);
                    }
            );
        }
    }
}
