package com.sparta.hh99_actualproject.dto.MemberInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
public class MemebrInfoFollowResponseDto {
    private String followMemberId;
    private String nickname;
    private String createdAt;
    private String color;
    private Integer totalPages;
}
