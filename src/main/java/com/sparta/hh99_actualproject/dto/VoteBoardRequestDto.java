package com.sparta.hh99_actualproject.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class VoteBoardRequestDto {
        private MultipartFile imgLeftFile;
        private MultipartFile imgRightFile;
        private String imgLeftTitle;
        private String imgRightTitle;
        private String title;
        private String contents;
}
