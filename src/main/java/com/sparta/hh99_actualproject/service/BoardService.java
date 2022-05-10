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
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public BoardResponseDto.DetailResponse getBoardDetails(Long boardPostId){
        Board board = boardRepository.findById(boardPostId).orElseThrow(
                ()-> new PrivateException(StatusCode.NOT_FOUND_POST)
        );

        //멤버
        String memberId = SecurityUtil.getCurrentMemberId();
        Member findedMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //이미지 리스트
        List<String> imgPathList = imgRepository.findAllByBoard(board)
                .stream()
                .map(Img::getImgUrl)
                .collect(Collectors.toList());

        BoardResponseDto.DetailResponse detailDto = BoardResponseDto.DetailResponse
                .builder()
                .boardPostId(board.getBoardPostId())
                .memberId(board.getMember().getMemberId())
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
//    public BoardResponseDto.DetailResponse createBoard(List<String> imgPaths, BoardRequestDto.SaveRequest requestDto) { 2205071820 변경
    public BoardResponseDto.DetailResponse createBoard(BoardRequestDto.SaveRequest requestDto) {
        //내용에 Null이 있으면 에러 발생 , 에러 발생시에 사진도 저장을 하면 안됨. (사진 저장을 createBoard 안으로 넣음)
        if (hasNullRequestData(requestDto)) {
            throw new PrivateException(StatusCode.NULL_INPUT_ERROR);
        }

        //이미지가 4장이 넘어가면 에러 발생
        if (requestDto.getFiles() != null && requestDto.getFiles().size() > 4) {
            throw new PrivateException(StatusCode.WRONG_INPUT_BOARD_IMAGE_NUM);
        }

        //Member 가져오기
        String memberId = SecurityUtil.getCurrentMemberId();
        Member findedMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //업로드된 사진 있으면 업로드 사진 저장 220507 1825 변경
        List<String> savedImgPaths = awsS3Service.uploadFiles(requestDto.getFiles());

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

        if (savedImgPaths != null) {
            for (String filePath : savedImgPaths) {
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

    public void updateBoard(Long boardPostId, BoardRequestDto.SaveRequest requestDto){
        // 데이터 공란이 안되게 확인
        if (hasNullRequestData(requestDto)) {
            throw new PrivateException(StatusCode.NULL_INPUT_ERROR);
        }
        //게시글 가져오기
        Board findedBoard = boardRepository.findById(boardPostId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_POST)
        );
        //게시글 작성자와 토큰 MemberId가 맞는지 확인
        String memberid = SecurityUtil.getCurrentMemberId(); // 멤버아이디 가져올때 시큐리티로 가져와야함
        if (!findedBoard.getMember().getMemberId().equals(memberid)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_POST_DELETE);
        }
        //Board 업데이트
        findedBoard.update(requestDto);
    }


    //게시글 수정 2205071800 변경
    @Transactional
    public void updateBoardWithIMGChange(Long boardPostId, BoardRequestDto.SaveRequest requestDto){
        // 데이터 공란이 안되게 확인
        if (hasNullRequestData(requestDto)) {
            throw new PrivateException(StatusCode.NULL_INPUT_ERROR);
        }

        //게시글 가져오기
        Board findedBoard = boardRepository.findById(boardPostId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_POST)
        );

        //게시글 작성자와 토큰 MemberId가 맞는지 확인
        String memberid = SecurityUtil.getCurrentMemberId(); // 멤버아이디 가져올때 시큐리티로 가져와야함
        if (!findedBoard.getMember().getMemberId().equals(memberid)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_POST_DELETE);
        }

        //업로드된 사진 있으면 업로드 사진 저장 ,없으면 null
        List<String> savedImgPaths = awsS3Service.uploadFiles(requestDto.getFiles());

        //기존에 이미지가 있는지 없는지 확인 필요
        List<Img> boardImgList = findedBoard.getImgList();

        //기존 있음 => 기존꺼를 삭제 (새로운거 : 이미 저장한 상태 [Return List<String>] or 없으면 [Return null])
        if(boardImgList != null){
            imgRepository.deleteAll(boardImgList);
            awsS3Service.deleteAll(boardImgList);
        }
        //기존 없음 -> 무시 (새로운거 : 이미 저장한 상태 [Return List<String>] or 없으면 [Return null])

        //Update 메서드 이므로 기존 Model 업데이트 필요 : 새로운 사진이 있으면 새로운 사진 주소로 기존 Model 업데이트 ,새로운 사진이 없으면 Null 값으로 기존 Model 업데이트
        boardImgList.clear(); // 기존값이 있으면 값이 있는 List, 값이 없으면 Size가 0인 empty List가 온다.
        if(savedImgPaths != null)
            boardImgList.addAll(imgModelUpdate(findedBoard, savedImgPaths));

        //Board 업데이트
        findedBoard.update(requestDto);
    }

    //이미지 업로드 하는애 2205071800 변경
    @Transactional
    public List<Img> imgModelUpdate(Board board , List<String> savedImgPaths) {
        if(savedImgPaths == null){
            return null;
        }

        List<Img> imgList = new ArrayList<>();

        for (String savedImgPath : savedImgPaths) {
            Img savedImg = imgRepository.save(Img.of(board, savedImgPath));
            imgList.add(savedImg);
        }

        return imgList;
    }




    //게시글삭제 2205071800 변경
    public void deleteBoard(Long boardPostId){
        Board board = boardRepository.findById(boardPostId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_POST)
        );
        String memberid = SecurityUtil.getCurrentMemberId(); // 멤버아이디 가져올때 시큐리티로 가져와야함

        //본인 게시글만 삭제가능하도록
        if (!board.getMember().getMemberId().equals(memberid)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_POST_DELETE);
        }


        awsS3Service.deleteAll(board.getImgList());
        boardRepository.delete(board);
        
    }
}

