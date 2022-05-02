package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.SelectionResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Selection;
import com.sparta.hh99_actualproject.model.VoteContent;
import com.sparta.hh99_actualproject.repository.SelectionRepository;
import com.sparta.hh99_actualproject.repository.VoteContentRepository;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class SelectionService {
    private final VoteContentRepository voteContentRepository;
    private final SelectionRepository selectionRepository;

    @Transactional
    public SelectionResponseDto selectVoteContent(Long postId, String selectionName) {
        //ImageTitle 을 기반으로 VoteContents 를 찾는다
        List<VoteContent> findedVoteContentList = voteContentRepository.findAllByImageTitle(selectionName);
        //VoteContents 가 없으면 에러 발생
        if (findedVoteContentList == null) {
            throw new PrivateException(StatusCode.WRONG_INPUT_VOTE_SELECTION);
        }

        //List 에서 PostId랑 동일한 VoteContents를 찾는다.
        for (VoteContent voteContent : findedVoteContentList) {
            if (voteContent.getVoteBoard().getVoteBoardId().equals(postId)) {
                Selection selection = Selection.builder()
                                                .voteContent(voteContent)
                                                .memberId(SecurityUtil.getCurrentMemberId())
                                                .build();

                selection = selectionRepository.save(selection);

                //찾은 VoteContent에 UserId를 추가한다.
                voteContent.getSelectionList().add(selection);
                break;
            }
        }

        SelectionResponseDto sleepResponseDto = new SelectionResponseDto();
        sleepResponseDto.setSelected(true);

        return sleepResponseDto;
    }
}
