package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.FollowResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Follow;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.model.NotiTypeEnum;
import com.sparta.hh99_actualproject.model.Notification;
import com.sparta.hh99_actualproject.repository.FollowRepository;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class FollowService {
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;

    @Transactional
    public FollowResponseDto followMember(String followMemberId, boolean follow) {
        //Follow 하려는 Member가 존재하는지 확인하기
        if (!memberRepository.existsByMemberId(followMemberId)) {
            throw new PrivateException(StatusCode.NOT_FOUND_MEMBER); //FollowMemberId가 존재하지 않음
        }

        //Member 가져오기
        String memberId = SecurityUtil.getCurrentMemberId();
        Member findedMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new PrivateException(StatusCode.NOT_FOUND_MEMBER)); //JWT 사용자 MemberId가 존재하지 않음

        Member followMember = memberRepository.findByMemberId(followMemberId)
                .orElseThrow(()-> new PrivateException(StatusCode.NOT_FOUND_MEMBER)); //JWT 사용자 MemberId가 존재하지 않음

        //Follow Entity에서 중복체크 필요. 이미 되어있으면 처리되면 X
        Follow findedFollow = followRepository.findByMemberAndFollowMemberId(findedMember, followMemberId)
                .orElse(null);

        FollowResponseDto followResponseDto = new FollowResponseDto();

        // 1. Follow = true  , findedFollow = 이미 존재   :  아무 처리 X , return = true
        // 2. Follow = false , findedFollow = null       :  아무 처리 X , return = false
        // 3. Follow = true  , findedFollow = null        :  추가
        // 4. Follow = false , findedFollow = 이미 존재  :  삭제

        //없으면 추가하기
        if(follow && findedFollow != null){ //1.
            followResponseDto.setFollow(true);
        }else if(!follow && findedFollow == null){ //2.
            followResponseDto.setFollow(false);
        }else if(follow && findedFollow == null){ //3.
            //Follow Table 에 추가하기
            followRepository.save(Follow.builder()
                    .member(findedMember)
                    .followMemberId(followMemberId)
                    .color(followMember.getColor())
                    .nickname(followMember.getNickname())
                    .build());
            followResponseDto.setFollow(true);
            Notification savedNotification = notificationService.saveNotification(followMemberId, NotiTypeEnum.FOLLOW,findedMember.getNickname(), null);
            //상대방의 color 전달해야해서 저장
            if (savedNotification != null) {
                savedNotification.setOppositeMemberColor(findedMember.getColor());
            }
        }else if(!follow && findedFollow != null){ //4.
            //Follow Table 에서 삭제
            followRepository.deleteById(findedFollow.getFollowId());
            followResponseDto.setFollow(false);
        }

        return followResponseDto;
    }
}
