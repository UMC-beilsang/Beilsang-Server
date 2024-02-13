package com.BeilsangServer.domain.member.service;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.repository.ChallengeRepository;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.domain.member.repository.ChallengeMemberRepository;
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

    /***
     * 하루동안 인증하지 않은 챌린지 멤버의 상태 수정
     * 스케줄러를 사용하여 피드가 올라가 있지 않은 챌린지 멤버의 상태 변경
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkFailure() {

        List<ChallengeMember> notUploadedMember = challengeMemberRepository.findAllByChallengeStatusAndIsFeedUpload(ChallengeStatus.ONGOING, false);

        for (ChallengeMember challengeMember : notUploadedMember) {

            Challenge challenge = challengeMember.getChallenge();

            // 지금까지 성공한 일수
            int remainSuccess = challenge.getTotalGoalDay() - challengeMember.getSuccessDays();
            // 챌린지 종료일까지 남은 일수
            int remainDay = challenge.getFinishDate().until(LocalDate.now()).getDays() + 1;

            // 남은 일수 확인 후 목표 일수를 다 채우면 성공, 남은 기간 내에 다 채우지 못하면 실패로 변경
            if (remainSuccess == 0) {
                challengeMember.updateChallengeStatus(ChallengeStatus.SUCCESS);
                challengeMemberRepository.save(challengeMember);
            } else if (remainDay > remainSuccess) {
                challengeMember.updateChallengeStatus(ChallengeStatus.FAIL);
                challengeMemberRepository.save(challengeMember);
            }
        }

        dailyMakeIsFeedUploadFalse();
        checkIsChallengeStart();
    }

    /***
     * 모든 챌린지 멤버의 당일 인증 상태 수정
     * 아직 끝나지 않은 챌린지만 대상이 되어야 함
     */
    @Transactional
    public void dailyMakeIsFeedUploadFalse() {

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
}
