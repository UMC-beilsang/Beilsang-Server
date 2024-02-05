package com.BeilsangServer.domain.notification.service;


import com.BeilsangServer.domain.notification.dto.NotificationRequestDto;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FCMService {
    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(NotificationRequestDto notificationRequest) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(notificationRequest.getDeviceToken())
                .setNotification(notificationRequest.toNotification())
                .build();

        firebaseMessaging.send(message);
    }
}
