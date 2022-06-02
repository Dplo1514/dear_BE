package com.sparta.hh99_actualproject.dto.MemberInfo;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class MemberInfoMessageResponseDto {
    private Long messageId;
    private LocalDateTime createdAt;
    private String reqMemberNickname;
    private String message;
    private Integer totalPages;
}
