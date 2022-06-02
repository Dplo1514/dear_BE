package com.sparta.hh99_actualproject.dto.ChatRoomDto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatRoomReqRequestDto {
    private List<MultipartFile> imgList;
    private String reqTitle;
    private String reqCategory;
    private String reqGender;
}
