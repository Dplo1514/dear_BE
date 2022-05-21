package com.sparta.hh99_actualproject.dto;

import com.sparta.hh99_actualproject.model.NotiTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationResponseDto {
    private NotiTypeEnum notiType;

    private Long notiPostId;

    private String notiContent;

    private LocalDateTime createAt;

    private boolean isRead;
}
