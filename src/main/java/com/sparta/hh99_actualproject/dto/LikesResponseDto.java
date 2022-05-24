package com.sparta.hh99_actualproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LikesResponseDto {
    private boolean likes;
    private List<String> memberIdList;
}
