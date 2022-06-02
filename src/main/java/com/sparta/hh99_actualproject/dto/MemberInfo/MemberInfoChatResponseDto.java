package com.sparta.hh99_actualproject.dto.MemberInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Setter
@Getter
public class MemberInfoChatResponseDto {
    private String reqComment;
    private String reqCategory;
    private String createdAt;
    private String chatTime;
    private String myRole;
    private String nickname;
    private String color;
}
