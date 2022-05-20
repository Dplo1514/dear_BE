package com.sparta.hh99_actualproject.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.sparta.hh99_actualproject.dto.NotificationResponseDto;
import com.sparta.hh99_actualproject.model.Notification;
import com.sparta.hh99_actualproject.repository.FCMTokenRepository;
import com.sparta.hh99_actualproject.repository.NotificationRepository;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    private NotificationRepository notificationRepository;
    private FCMTokenRepository fcmTokenRepository;

    public int getUnReadAlarmNum(String fcmDeviceToken) {
        //Map에 ID 랑 Token 저장
        String memberId = SecurityUtil.getCurrentMemberId();
        if (fcmDeviceToken != null) {
            fcmTokenRepository.save(memberId, fcmDeviceToken);
        }

        return  notificationRepository.countByMemberIdAndIsRead(memberId, false);
    }

    @Transactional
    public List<NotificationResponseDto> getAlarmAllList() {
        String memberId = SecurityUtil.getCurrentMemberId();

        List<Notification> notificationList = notificationRepository.findAllByMemberId(memberId);

        List<NotificationResponseDto> notificationResponseDtoList = new ArrayList<>();
        for (Notification notification : notificationList) {
            notificationResponseDtoList.add(NotificationResponseDto.builder()
                                                                    .notiType(notification.getNotiType())
                                                                    .notiContent(notification.getNotiContent())
                                                                    .isRead(notification.isRead())
                                                                    .build());
        }

        //Get 요청을 했다는 것은 내용을 읽었다고 판단을 해야함
        for(Notification notification : notificationList){
            if (!notification.isRead()) {
                notification.setRead(true);
            }
        }

        return notificationResponseDtoList;
    }

    public void sendFcmMsg(String MsgContents){
        String memberId = SecurityUtil.getCurrentMemberId();
        // This registration token comes from the client FCM SDKs.
        String registrationToken = fcmTokenRepository.findById(memberId);
        
        // See documentation on defining a message payload.
        Message message = Message.builder()
                .putData("type", "test")
                .putData("content", MsgContents)
                .setToken(registrationToken)
                .build();

        // Send a message to the device corresponding to the provided
        // registration token.
        try {
            String response = FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            System.out.println("FCM MSG 전송에 실패했습니다");
            throw new RuntimeException(e);
        }
        // Response is a message ID string.
        System.out.println("Successfully sent fcm message");
    }

}
