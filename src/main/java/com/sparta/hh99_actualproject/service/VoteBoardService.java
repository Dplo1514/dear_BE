package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.BoardRequestDto;
import com.sparta.hh99_actualproject.dto.VoteBoardRequestDto;
import com.sparta.hh99_actualproject.dto.VoteBoardResponseDto;
import com.sparta.hh99_actualproject.dto.VoteContentResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.model.Selection;
import com.sparta.hh99_actualproject.model.VoteBoard;
import com.sparta.hh99_actualproject.model.VoteContent;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.repository.SelectionRepository;
import com.sparta.hh99_actualproject.repository.VoteBoardRepository;
import com.sparta.hh99_actualproject.repository.VoteContentRepository;
import com.sparta.hh99_actualproject.service.validator.Validator;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@AllArgsConstructor
@Service
public class VoteBoardService {
    private final Validator validator;
    private final VoteBoardRepository voteBoardRepository;
    private final VoteContentRepository voteContentRepository;
    private final MemberRepository memberRepository;
    private final SelectionRepository selectionRepository;
    private final AwsS3Service awsS3Service;


    @Transactional
    public VoteBoardResponseDto createVoteBoard(VoteBoardRequestDto requestDto) {
        //null Check
        if (validator.hasNullDtoField(requestDto)){
            throw new PrivateException(StatusCode.NULL_INPUT_ERROR);
        }

        //Member 가져오기
        String memberId = SecurityUtil.getCurrentMemberId();
        Member findedMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        List<MultipartFile> multipartFileList = Arrays.asList(requestDto.getImgLeftFile(), requestDto.getImgRightFile());
        List<String> savedImgPaths = awsS3Service.uploadFiles(multipartFileList);

        String  imgLeftFilePath = null ,imgRightFilePath = null;
        if(savedImgPaths != null) {
            imgLeftFilePath = savedImgPaths.get(0);
            imgRightFilePath = savedImgPaths.get(1);
        }

        //VoteBoard 제작하기
        VoteBoard savedVoteBoard = voteBoardRepository.save(VoteBoard.of(findedMember,requestDto));

        //Dto를 VoteContents Model 로 변경
        VoteContent leftVoteContent = VoteContent.builder()
                .voteBoard(savedVoteBoard)
                .imageUrl(imgLeftFilePath)
                .imageTitle(requestDto.getImgLeftTitle())
                .build();
        leftVoteContent = voteContentRepository.save(leftVoteContent);

        VoteContent rightVoteContent = VoteContent.builder()
                .voteBoard(savedVoteBoard)
                .imageUrl(imgRightFilePath)
                .imageTitle(requestDto.getImgRightTitle())
                .build();
        rightVoteContent = voteContentRepository.save(rightVoteContent);

        //VoteContents 를 만들어서 VoteBoard에 할당 변경
        List<VoteContent> voteContentList = Arrays.asList(leftVoteContent,rightVoteContent);

        savedVoteBoard.setVoteContentList(voteContentList);

        //VoteBoardResponseDto 를 만들기 위해서 List<VoteContentResponseDto>를 만들자
        List<VoteContentResponseDto> voteContentResponseDtoList = Arrays.asList(VoteContentResponseDto.of(leftVoteContent), VoteContentResponseDto.of(rightVoteContent));

        //VoteBoardResponseDto return 해주기
        return VoteBoardResponseDto.builder()
                .postId(savedVoteBoard.getVoteBoardId())
                .memberId(memberId)
                .vote(voteContentResponseDtoList)
                .createdAt(savedVoteBoard.getCreatedAt())
                .title(savedVoteBoard.getTitle())
                .contents(savedVoteBoard.getContents())
                .build();
    }

    public VoteBoardResponseDto getVoteBoard(Long postId) {
        final int LEFT_VOTE_CONTENT_NUM = 0;
        final int RIGHT_VOTE_CONTENT_NUM = 1;
        final int LEFT_VOTE_CONTENT_SELECTION_NUM = 1;
        final int RIGHT_VOTE_CONTENT_SELECTION_NUM = 2;
        //VoteBoard 가져오기
        VoteBoard findedVoteBoard = voteBoardRepository.findById(postId)
                .orElseThrow(() -> new PrivateException(StatusCode.NOT_FOUND_POST));

        //VoteContent 가져오기
        List<VoteContent> voteContentList = findedVoteBoard.getVoteContentList();
        List<VoteContentResponseDto> voteContentResponseDtoList = null;

        //leftVoteContent 가져오기
        VoteContent leftVoteContent = voteContentList.get(LEFT_VOTE_CONTENT_NUM);
        //Selection List 확인
        List<Selection> leftVoteContentSelectionList = selectionRepository.findAllByVoteBoardIdAndSelectionNum(postId, LEFT_VOTE_CONTENT_SELECTION_NUM);
        //MerberId List 가져오기
        List<String> leftVoteContentMemberIdList = getMemberIdListInVoteSelectionList(leftVoteContentSelectionList);

        //rightVoteContent 가져오기
        VoteContent rightVoteContent = voteContentList.get(RIGHT_VOTE_CONTENT_NUM);
        //Selection List 확인
        List<Selection> rightVoteContentSelectionList = selectionRepository.findAllByVoteBoardIdAndSelectionNum(postId, RIGHT_VOTE_CONTENT_SELECTION_NUM);
        //MerberId List 가져오기
        List<String> rightVoteContentMemberIdList = getMemberIdListInVoteSelectionList(rightVoteContentSelectionList);

        voteContentResponseDtoList = Arrays.asList(VoteContentResponseDto.of(leftVoteContent,leftVoteContentMemberIdList), VoteContentResponseDto.of(rightVoteContent,rightVoteContentMemberIdList));

        return VoteBoardResponseDto.builder()
                .postId(findedVoteBoard.getVoteBoardId())
                .memberId(findedVoteBoard.getMember().getMemberId())
                .vote(voteContentResponseDtoList)
                .createdAt(findedVoteBoard.getCreatedAt())
                .title(findedVoteBoard.getTitle())
                .contents(findedVoteBoard.getContents())
                .build();
    }

    private List<String> getMemberIdListInVoteSelectionList(List<Selection> selectionList) {
        List<String> memberIdList = new ArrayList<>();
        for (Selection selection : selectionList) {
            memberIdList.add(selection.getMemberId());
        }

        return memberIdList;
    }

    @Transactional
    public void deleteVoteBoard(Long postId) {
        //자기 게시글이 아니면 지울수없다.
            //1.게시글 가져오기
        VoteBoard findedVoteBoard = voteBoardRepository.findById(postId)
                .orElseThrow(() -> new PrivateException(StatusCode.NOT_FOUND_POST));
            //2.자기 게시글이 맞는지 확인
        String memberId = SecurityUtil.getCurrentMemberId();
        if(!findedVoteBoard.getMember().getMemberId().equals(memberId)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_POST_DELETE);
        }

        //Selection 삭제 [postId가 사라지므로 얘를 먼저 지워야함]
        selectionRepository.deleteAllByVoteBoardId(postId);
        //post 삭제 시에 Contents도 같이 삭제되는지 확인 필요.
        voteBoardRepository.deleteById(postId);
    }
}
