package com.sparta.hh99_actualproject.dto.ChatRoomDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Builder
@Getter
public class ChatRoomMatchResponseDto {
    private String sessionId;
    private String token;
    private String role;
}
