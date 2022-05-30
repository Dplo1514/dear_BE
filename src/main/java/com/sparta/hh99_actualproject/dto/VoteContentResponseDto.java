package com.sparta.hh99_actualproject.dto;

import com.sparta.hh99_actualproject.model.VoteContent;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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

    public static VoteContentResponseDto of(VoteContent voteContent, List<String> voteContentMemberIdList, String memberId) {
        return VoteContentResponseDto.builder()
                .imageUrl(voteContent.getImageUrl())
                .imageTitle(voteContent.getImageTitle())
                .selectionList(voteContentMemberIdList)
                .selected(memberId != null && voteContentMemberIdList.contains(memberId))
                .build();
    }
}
