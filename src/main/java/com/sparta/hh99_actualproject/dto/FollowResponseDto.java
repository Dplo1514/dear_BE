package com.sparta.hh99_actualproject.dto;

import lombok.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class FollowResponseDto {
    private boolean follow;
}
