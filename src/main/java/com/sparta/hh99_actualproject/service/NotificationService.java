package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.NotificationResponseDto;
import com.sparta.hh99_actualproject.dto.UnReadAlarmResponseDto;
import com.sparta.hh99_actualproject.model.Message;
import com.sparta.hh99_actualproject.model.Notification;
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

    public UnReadAlarmResponseDto getUnReadAlarmNum() {
        String memberId = SecurityUtil.getCurrentMemberId();
        long unReadAlarmNum = notificationRepository.countByMemberIdAndIsRead(memberId, false);

        return UnReadAlarmResponseDto.builder().unReadAlarmNum(unReadAlarmNum).build();
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
}
