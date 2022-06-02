package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.MainBoardResponseDto;
import com.sparta.hh99_actualproject.dto.MemberMainResponseDto;
import com.sparta.hh99_actualproject.dto.ServiceCommentResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.model.Score;
import com.sparta.hh99_actualproject.model.ServiceComment;
import com.sparta.hh99_actualproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MainService {
    private final MemberRepository memberRepository;
    private final ScoreRepository scoreRepository;
    private final ResponseTagService responseTagService;
    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;
    private final ServiceCommentRepository serviceCommentRepository;

    public List<MemberMainResponseDto> getRankMember() {
        //score기준으로 정렬한 score를 모두 찾아온다.
        List<Score> findRankScore = scoreRepository.findAllByOrderByScoreDesc();

        List<MemberMainResponseDto> memberMainResponseDtoList = new ArrayList<>(5);

        for (Score score : findRankScore) {
            Member findRankMember = memberRepository.findByMemberId(score.getMemberId())
                    .orElseThrow(() -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

            //멤버 한명한명의 가장 많이 획득한 ResTag를 찾아온다.
            try {
                String memberMostResTag = responseTagService.findMemberMostResTag(findRankMember.getMemberId()).getResTag1();
                MemberMainResponseDto memberMainResponseDto = MemberMainResponseDto.builder()
                        .nickname(findRankMember.getNickname())
                        .color(findRankMember.getColor())
                        .score(score.getScore())
                        .resTag1(memberMostResTag)
                        .build();

                memberMainResponseDtoList.add(memberMainResponseDto);

                if (memberMainResponseDtoList.size() >= 5) {
                    break;
                }
            }catch (NullPointerException e){
                MemberMainResponseDto memberMainResponseDto = MemberMainResponseDto.builder()
                        .nickname(findRankMember.getNickname())
                        .color(findRankMember.getColor())
                        .score(score.getScore())
                        .build();

                memberMainResponseDtoList.add(memberMainResponseDto);

                if (memberMainResponseDtoList.size() >= 5) {
                    break;
                }
            }
        }
        return memberMainResponseDtoList;
    }


    public List<MainBoardResponseDto> getRankingBoard() {
        List<Long> top4boardId = likesRepository.findTop4BoardIdOrderByTotalLike();
        List<MainBoardResponseDto> mainBoardResponseDtoList = new ArrayList<>(4);

        for (Long boardId : top4boardId) {
            Board board = boardRepository.findById(boardId).orElseThrow(
                    () -> new PrivateException(StatusCode.NOT_FOUND_POST));
            MainBoardResponseDto mainBoardResponseDto = MainBoardResponseDto.builder()
                    .postId(board.getBoardPostId())
                    .category(board.getCategory())
                    .title(board.getTitle())
                    .likes(board.getLikesList().size())
                    .comments(board.getCommentList().size())
                    .build();
            mainBoardResponseDtoList.add(mainBoardResponseDto);
        }
        return mainBoardResponseDtoList;
    }

    public List<ServiceCommentResponseDto> getServiceReview() {
        List<ServiceComment> serviceCommentList = serviceCommentRepository.findAllByOrderByCreatedAtDesc();
        //최근작성 스무개
        List<ServiceCommentResponseDto> serviceCommentResponseDtoList = new ArrayList<>();
        ServiceCommentResponseDto serviceCommentResponseDto = new ServiceCommentResponseDto();
        for (ServiceComment serviceComment : serviceCommentList) {
            serviceCommentResponseDto = ServiceCommentResponseDto.builder()
                    .userId(serviceComment.getMemberId())
                    .nickname(serviceComment.getNickname())
                    .comment(serviceComment.getServiceComment())
                    .createdAt(String.valueOf(serviceComment.getCreatedAt()))
                    .build();
            serviceCommentResponseDtoList.add(serviceCommentResponseDto);

            if (serviceCommentResponseDtoList.size() >= 20){
                break;
            }

        }
        return serviceCommentResponseDtoList;
    }
}
