package com.sparta.hh99_actualproject.dto;

import com.sparta.hh99_actualproject.model.VoteContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.springframework.lang.Nullable;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class VoteContentResponseDto {
    private String imageUrl;
    private String imageTitle;
    private List<String> selectionList;
    private boolean selected;

    public static VoteContentResponseDto of(VoteContent voteContent) {
        return VoteContentResponseDto.builder()
                .imageUrl(voteContent.getImageUrl())
                .imageTitle(voteContent.getImageTitle())
                .build();
    }

    public static VoteContentResponseDto of(VoteContent voteContent, List<String> voteContentMemberIdList) {
        return VoteContentResponseDto.builder()
                .imageUrl(voteContent.getImageUrl())
                .imageTitle(voteContent.getImageTitle())
                .selectionList(voteContentMemberIdList)
                .selected(voteContent.isSelected())
                .build();
    }
}
