package com.sparta.hh99_actualproject.dto;

import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class MessageRequestDto {
    private String resUserId;
    private String message;
}
