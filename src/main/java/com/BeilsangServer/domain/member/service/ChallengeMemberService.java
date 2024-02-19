package com.BeilsangServer.domain.member.service;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.repository.ChallengeRepository;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.ChallengeMemberRepository;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.BeilsangServer.domain.point.entity.PointLog;
import com.BeilsangServer.domain.point.repository.PointLogRepository;
import com.BeilsangServer.global.enums.ChallengeStatus;
import com.BeilsangServer.global.enums.PointName;
import com.BeilsangServer.global.enums.PointStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeMemberService {

    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;
    private final PointLogRepository pointLogRepository;

    /***
     * 스케줄러를 사용하여 매일 00시 정각마다 작업을 수행
     */
    @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(fixedRate = 60000)
    public void dailyTasks() {

        System.out.println("=================================");
        System.out.println("ChallengeMemberService.dailyTasks");
        System.out.println("=================================");

        checkFailure();
        checkSuccess();
        makeIsFeedUploadFalse();
        checkIsChallengeStart();
    }

    /***
     * 하루동안 인증하지 않은 챌린지 멤버의 상태 수정
     * 피드가 올라가 있지 않은 챌린지 멤버의 상태 변경
     */
    public void checkFailure() {

        System.out.println("=================================");
        System.out.println("ChallengeMemberService.checkFailure");
        System.out.println("=================================");

        List<ChallengeMember> notUploadedMember = challengeMemberRepository.findAllByChallengeStatusAndIsFeedUpload(ChallengeStatus.ONGOING, false);

        for (ChallengeMember challengeMember : notUploadedMember) {

            Challenge challenge = challengeMember.getChallenge();

            // 지금까지 성공한 일수
            int remainSuccess = challenge.getTotalGoalDay() - challengeMember.getSuccessDays();
            // 챌린지 종료일까지 남은 일수
            int remainDay = LocalDate.now().until(challenge.getFinishDate()).getDays() + 1;

            // 남은 일수 확인 후 목표 일수를 다 채우면 성공, 남은 기간 내에 다 채우지 못하면 실패로 변경
            if (remainSuccess <= 0) {
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
    public void makeIsFeedUploadFalse() {

        System.out.println("=================================");
        System.out.println("ChallengeMemberService.makeIsFeedUploadFalse");
        System.out.println("=================================");

        // 매일 정시마다 모든 챌린지 멤버의 인증 상태 수정
        challengeMemberRepository.findAllByChallengeStatus(ChallengeStatus.ONGOING).forEach(challengeMember -> {
            challengeMember.makeIsFeedUploadFalse();
            challengeMemberRepository.save(challengeMember);
        });
    }

    /***
     * 시작하지 않은 챌린지의 상태를 진행 중으로 변경
     */
    public void checkIsChallengeStart() {

        System.out.println("=================================");
        System.out.println("ChallengeMemberService.checkIsChallengeStart");
        System.out.println("=================================");

        List<ChallengeMember> notYetChallengeMembers = challengeMemberRepository.findAllByChallengeStatus(ChallengeStatus.NOT_YET);

        notYetChallengeMembers.stream()
                .filter(challengeMember -> !challengeMember.getChallenge().getStartDate().isAfter(LocalDate.now()))
                .forEach(challengeMember -> {
                    challengeMember.updateChallengeStatus(ChallengeStatus.ONGOING);
                    challengeMemberRepository.save(challengeMember);
                });
    }

    /***
     * 피드 생성 시 챌린지 인증 처리
     * @param challengeMember 피드를 생성한 주체
     */
    public void checkFeedUpload(ChallengeMember challengeMember) {

        System.out.println("=================================");
        System.out.println("ChallengeMemberService.checkFeedUpload");
        System.out.println("=================================");

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

        System.out.println("=================================");
        System.out.println("ChallengeMemberService.checkSuccess");
        System.out.println("=================================");

        List<Challenge> finishedChallenges = challengeRepository.findAllByFinishDate(LocalDate.now().minusDays(1));

        for (Challenge challenge : finishedChallenges) {

            List<ChallengeMember> successMembers = challengeMemberRepository.findAllByChallengeId(challenge.getId())
                    .stream()
                    .filter(challengeMember -> challengeMember.getChallengeStatus().equals(ChallengeStatus.SUCCESS))
                    .toList();

            // 성공한 멤버가 없는 경우 종료
            if (successMembers.isEmpty()) return;

            int pointDivide = challenge.getCollectedPoint() / successMembers.size();
            int liftedPoint = pointDivide + (100 - pointDivide % 100); // 100원 단위로 돌려주기 위해 올림
            int hostPoint = liftedPoint * 2; // 호스트의 경우 2배 지급

            successMembers.forEach(challengeMember -> {

                Member member = challengeMember.getMember();

                if (challengeMember.getIsHost()) { // 호스트인 경우
                    member.addPoint(hostPoint);
                    memberRepository.save(member);

                    // 포인트 기록 생성 및 디비 저장
                    pointLogRepository.save(PointLog.builder()
                            .pointName(PointName.SUCCESS_CHALLENGE_HOST)
                            .status(PointStatus.EARN)
                            .value(hostPoint)
                            .member(member)
                            .build());
                } else { // 호스트가 아닌 경우
                    member.addPoint(liftedPoint);
                    memberRepository.save(member);

                    // 포인트 기록 생성 및 디비 저장
                    pointLogRepository.save(PointLog.builder()
                            .pointName(PointName.SUCCESS_CHALLENGE)
                            .status(PointStatus.EARN)
                            .value(liftedPoint)
                            .member(member)
                            .build());
                }
            });
        }
    }
}
