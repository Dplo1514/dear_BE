package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.VoteBoardResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Selection;
import com.sparta.hh99_actualproject.repository.SelectionRepository;
import com.sparta.hh99_actualproject.repository.VoteBoardRepository;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SelectionService {
    private final SelectionRepository selectionRepository;
    private final VoteBoardRepository voteBoardRepository;
    private final VoteBoardService voteBoardService;

    @Transactional
    public VoteBoardResponseDto selectVoteContent(Long postId, Integer selectionNum) {
        if(!voteBoardRepository.existsById(postId)){
            throw new PrivateException(StatusCode.NOT_FOUND_POST);
        }
        String memberId = SecurityUtil.getCurrentMemberId();

        //이전에 투표한게 있는지 확인한다.
        Selection selection = selectionRepository.findByVoteBoardIdAndMemberId(postId,memberId)
                .orElse(null);
        
        //투표를 처음하는 상황
        if(selection == null){
            selectionRepository.save(Selection.builder()
                    .voteBoardId(postId)
                    .memberId(memberId)
                    .selectionNum(selectionNum)
                    .build());
        }
        //투표를 한번 이상 했음
        else{
            selection.setSelectionNum(selectionNum);
        }

        return voteBoardService.getVoteBoard(postId, memberId);
    }

//    public List<Selection> getSelectionList(VoteContent voteContent) {
//        return selectionRepository.findAllByVoteContent(voteContent);
//    }
}
