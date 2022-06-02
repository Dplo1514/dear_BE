package com.sparta.hh99_actualproject.dto.MemberInfo;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class MemberInfoMessageResponseDto {
    private Long messageId;
    private LocalDateTime createdAt;
    private String reqMemberNickname;
    private String message;
    private Integer totalPages;
}
