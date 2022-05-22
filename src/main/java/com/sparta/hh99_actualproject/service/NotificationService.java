package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.NotificationResponseDto;
import com.sparta.hh99_actualproject.dto.UnReadAlarmResponseDto;
import com.sparta.hh99_actualproject.model.NotiTypeEnum;
import com.sparta.hh99_actualproject.model.Notification;
import com.sparta.hh99_actualproject.repository.NotificationRepository;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
                                                                    .oppositeMemberColor(notification.getOppositeMemberColor())
                                                                    .notiPostId(notification.getNotiPostId())
                                                                    .createAt(notification.getCreatedAt())
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

    public Notification saveNotification(String memberId , NotiTypeEnum notiType , String notiContent , Long notiPostId){
        if(notiType.toString().equals("FOLLOW") || notiType.toString().equals("MESSAGE")){
            //팔로우 및 메세지는 PostId가 들어가지 않음.
            notiPostId = null;
        }

        //댓글 채택 및 팔로우를 연속해서 껏다가 켯다가를 할 수 있으므로 중복된 알람을 막기 위한 목적으로 설정
        if(notiType.toString().equals("FOLLOW") || notiType.toString().equals("CHOICE")){
            //먼저 동일 내용의 알람이 있는지 찾는다.
            Notification findedNotification = notificationRepository.findTopByMemberIdAndNotiTypeAndNotiContentOrderByCreatedAtDesc(memberId, notiType,notiContent).orElse(null);
            //이전에 존재하는 알람이 적당한 시간 간격을 가졌는지 확인한다.
            if(!isValidNotiAlarmTimeInterval(findedNotification)){
                //유효하지 않으면 알람을 저장하지 않고 그냥 Return
                return null;
            }
        }

        //알람 기능을 위해 알람 내용을 DB에 추가
        return notificationRepository.save(Notification.builder()
                .memberId(memberId)
                .notiType(notiType)
                .notiContent(notiContent)
                .notiPostId(notiPostId)
                .build());
    }

    private boolean isValidNotiAlarmTimeInterval(Notification findedNotification){
        //해당 하는 알람이 없으면 알람 저장 OK
        if (findedNotification == null) {
            return true;
        }
        // 알람이 저장된 시간을 가져옴
        LocalDateTime savedTime = findedNotification.getCreatedAt();

        // 현재 시간
        LocalDateTime nowTime = LocalDateTime.now();

        //최종 알람으로부터 1분이 지났으면 True.  오는 알람이면 유효하지 않은 거임
        return nowTime.isAfter(savedTime.plusMinutes(1L));
    }
}
