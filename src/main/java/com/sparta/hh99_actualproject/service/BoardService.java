package com.sparta.hh99_actualproject.service;


import com.sparta.hh99_actualproject.dto.BoardRequestDto;
import com.sparta.hh99_actualproject.dto.BoardResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.Img;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.repository.BoardRepository;
import com.sparta.hh99_actualproject.repository.ImgRepository;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BoardService {
    private final AwsS3Service awsS3Service;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ImgRepository imgRepository;

    //게시글 전체조회
    @Transactional
    public List<BoardResponseDto.MainResponse> getAllBoard() {
        List<Board> boards = boardRepository.findAllByOrderByCreatedAtDesc();
        List<BoardResponseDto.MainResponse> boardResponse = new ArrayList<>();
        for (Board board : boards) {
            BoardResponseDto.MainResponse boardDto = BoardResponseDto.MainResponse
                    .builder()
                    .boardPostId(board.getBoardPostId())
                    .createAt(board.getCreatedAt())
                    .title(board.getTitle())
                    .build();
            boardResponse.add(boardDto);
        }
        return boardResponse;
    }


    //게시글 상세조회
    public BoardResponseDto.DetailResponse getBoardDetails(Long boardPostId) {
        Board board = boardRepository.findById(boardPostId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_POST)
        );

        //멤버
        String memberId = SecurityUtil.getCurrentMemberId();
        Member findedMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //이미지 리스트
        List<String> imgPathList = imgRepository.findAllByBoard(board)
                .stream()
                .map(Img::getImgUrl)
                .collect(Collectors.toList());

        BoardResponseDto.DetailResponse detailDto = BoardResponseDto.DetailResponse
                .builder()
                .boardPostId(board.getBoardPostId())
                .memberId(memberId)
                .category(board.getCategory())
                .contents(board.getContents())
                .createAt(board.getCreatedAt())
                .title(board.getTitle())
                .imgUrl(imgPathList)
                .build();

        return detailDto;
    }


    //게시글 작성
    @Transactional
    public BoardResponseDto.DetailResponse createBoard(List<String> imgPaths, BoardRequestDto.SaveRequest requestDto) {
        //내용에 Null이 있으면 에러 발생
        if (hasNullRequestData(requestDto)) {
            throw new PrivateException(StatusCode.NULL_INPUT_ERROR);
        }

        //Member 가져오기
        String memberId = SecurityUtil.getCurrentMemberId();
        Member findedMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        String title = requestDto.getTitle();
        String category = requestDto.getCategory();
        String contents = requestDto.getContents();

        Board board = Board.builder()
                .member(findedMember)
                .title(title)
                .contents(contents)
                .category(category)
                .build();

        board = boardRepository.save(board);

        //img Table에 img 저장하기
        List<Img> imgList = new ArrayList<>();
        List<String> imgPathList = new ArrayList<>();

        if (imgPaths != null) {
            for (String filePath : imgPaths) {
                Img img = imgRepository.save(Img.of(board, filePath));
                imgList.add(img);
                imgPathList.add(filePath);
            }
        }

        board.setImgList(imgList);

        return BoardResponseDto.DetailResponse.builder()
                .boardPostId(board.getBoardPostId())
                .memberId(memberId)
                .createAt(board.getCreatedAt())
                .title(board.getTitle())
                .contents(board.getContents())
                .category(board.getCategory())
                .imgUrl(imgPathList)
                .build();
    }


    private boolean hasNullRequestData(BoardRequestDto.SaveRequest requestDto) {
        return requestDto.getTitle() == null ||
                requestDto.getContents() == null ||
                requestDto.getCategory() == null ||
                requestDto.getTitle().trim().equals("") ||
                requestDto.getContents().trim().equals("") ||
                requestDto.getCategory().trim().equals("");
    }


    //게시글삭제
    public void deleteBoard(Long boardPostId){
        Board board = boardRepository.findById(boardPostId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_POST)
        );
        String memberid = SecurityUtil.getCurrentMemberId(); // 멤버아이디 가져올때 시큐리티로 가져와야함

        //본인 게시글만 삭제가능하도록
        if (!board.getMember().getMemberId().equals(memberid)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_POST_DELETE);
        }


        awsS3Service.delete(board.getImgList());
//        awsS3Service.deleteFile("0032ad07-18c0-4dfd-a60c-869f95ea2043.png");
        boardRepository.delete(board);


    }




}
