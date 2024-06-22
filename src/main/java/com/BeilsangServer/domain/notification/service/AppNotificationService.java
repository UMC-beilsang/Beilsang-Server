package com.BeilsangServer.domain.notification.service;

import com.BeilsangServer.domain.challenge.entity.Challenge;
import com.BeilsangServer.domain.challenge.repository.ChallengeRepository;
import com.BeilsangServer.domain.member.entity.ChallengeMember;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.ChallengeMemberRepository;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.BeilsangServer.domain.notification.convertor.NotificationConvertor;
import com.BeilsangServer.domain.notification.dto.NotificationRequestDto;
import com.BeilsangServer.domain.notification.entity.AppNotification;
import com.BeilsangServer.domain.notification.entity.ChallengeNotification;
import com.BeilsangServer.domain.notification.entity.CommonNotification;
import com.BeilsangServer.domain.notification.repository.AppNotificationRepository;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.BeilsangServer.domain.notification.dto.NotificationResponseDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppNotificationService {
    private final MemberRepository memberRepository;
    private final AppNotificationRepository notificationRepository;
    private final FCMService fcmService;
    private final ChallengeRepository challengeRepository;
    private final ChallengeMemberRepository challengeMemberRepository;


    public List<NotificationDto> getNotification(Long memberId) {

        List<NotificationDto> response = new ArrayList<>();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 멤버"));

        // 해당 멤버가 읽지않은 모든 알림 시간순으로 찾기
        List<AppNotification> notifications = notificationRepository.findAllByMemberAndIsReadOrderByCreatedAtDesc(member,false);

        for(AppNotification notification : notifications){
            NotificationDto notificationDto = NotificationConvertor.toNotificationDto(notification);
            response.add(notificationDto);
        }
        return response;
    }

    @Transactional
    public void readNotification(Long notificationId){
        AppNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 알림"));

        notification.setIsRead();
    }

    @Scheduled(cron = "0 */1 0 * * *")
    @Transactional
    public void sendPushNotification() throws FirebaseMessagingException {

        sendStartChallengePushNotification();
        //sendRecommendedChallengePushNotification();
    }

    public void sendStartChallengePushNotification() throws FirebaseMessagingException {

        List<Challenge> challenges = challengeRepository.findAllByStartDate(LocalDate.now());
        for(Challenge challenge: challenges){
            List<ChallengeMember> members = challengeMemberRepository.findAllByChallengeId(challenge.getId());
            for(ChallengeMember member: members){
                String deviceToken = member.getMember().getDeviceToken();

                fcmService.sendNotification(NotificationRequestDto.builder()
                        .deviceToken(deviceToken)
                        .title("참여 챌린지 시작 알림")
                        .body("챌린지가 시작되었습니다.")
                        .build());

                notificationRepository.save(ChallengeNotification.builder()
                        .challengeId(challenge.getId())
                        .title("참여 챌린지 시작 알림")
                        .contents("챌린지가 시작되었습니다.")
                        .isRead(false)
                        .member(member.getMember())
                        .build());
            }
        }
    }

    public void sendRecommendedChallengePushNotification() throws FirebaseMessagingException {

        List<Member> members = memberRepository.findAll();
        for(Member member : members){
            String deviceToken = member.getDeviceToken();

            fcmService.sendNotification(NotificationRequestDto.builder()
                    .deviceToken(deviceToken)
                    .title("추천 챌린지 알림")
                    .body("관심 있을만한 챌린지를 확인해보세요.")
                    .build());

            notificationRepository.save(CommonNotification.builder()
                    .title("추천 챌린지 알림")
                    .contents("관심 있을만한 챌린지를 확인해보세요.")
                    .isRead(false)
                    .member(member)
                    .build());
        }

    }

    //@Scheduled(cron = "0 18 0 * * *")
    public void sendFeedUploadPushNotification() throws FirebaseMessagingException {

        List<Member> members = memberRepository.findAll();
        for(Member member : members){

            List<ChallengeMember> challengeMembers = challengeMemberRepository.findAllByMemberId(member.getId());
            boolean isUploaded = true;
            for(ChallengeMember challengeMember : challengeMembers) {
                if (!challengeMember.getIsFeedUpload())
                    isUploaded = false;
            }

            String deviceToken = member.getDeviceToken();

            if(!isUploaded){

                fcmService.sendNotification(NotificationRequestDto.builder()
                        .deviceToken(deviceToken)
                        .title("챌린지 인증 알림")
                        .body("오늘 챌린지 인증을 꼭 잊지 마세요.")
                        .build());

                notificationRepository.save(CommonNotification.builder()
                        .title("챌린지 인증 알림")
                        .contents("오늘 챌린지 인증을 꼭 잊지 마세요.")
                        .isRead(false)
                        .member(member)
                        .build());
            }
        }
    }
}

