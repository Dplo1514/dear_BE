package com.sparta.hh99_actualproject.service;


import com.sparta.hh99_actualproject.dto.BoardRequestDto;
import com.sparta.hh99_actualproject.dto.BoardResponseDto;
import com.sparta.hh99_actualproject.dto.LikesResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.Img;
import com.sparta.hh99_actualproject.model.Likes;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.repository.BoardRepository;
import com.sparta.hh99_actualproject.repository.ImgRepository;
import com.sparta.hh99_actualproject.repository.LikesRepository;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BoardService {
    private final AwsS3Service awsS3Service;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ImgRepository imgRepository;
    private final LikesRepository likesRepository;

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

    @Transactional
    public BoardResponseDto.DetailResponse updateBoard(Long boardPostId, BoardRequestDto.SaveRequest requestDto){
        // 데이터 공란이 안되게 확인
        if (hasNullRequestData(requestDto)) {
            throw new PrivateException(StatusCode.NULL_INPUT_ERROR);
        }

        //게시글 가져오기
        Board findedBoard = boardRepository.findById(boardPostId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_POST)
        );

        //게시글 작성자와 토큰 MemberId가 맞는지 확인
        String memberId = SecurityUtil.getCurrentMemberId(); // 멤버아이디 가져올때 시큐리티로 가져와야함
        if (!findedBoard.getMember().getMemberId().equals(memberId)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_POST_DELETE);
        }

        //기존 사진 확인하기
        List<Img> existedBoardImgList = findedBoard.getImgList(); //emptyList , 존재
        //신규 사진 확인하기
        List<MultipartFile> newFileList = requestDto.getFiles() == null ? Collections.emptyList() : requestDto.getFiles(); //emptyList , 존재
        //프론트에서 넘겨준 existedURL (기존 사진을 그대로 사용하는 부분 )
        List<String> existedUrlListFromFront = requestDto.getExistedURL() == null ? Collections.emptyList() : requestDto.getExistedURL(); //emptyList , 존재

        //기존 사진 보다 existedURL 수가 많으면 그건 말이 안되는 상황 => 에러
        if (existedBoardImgList.size() < existedUrlListFromFront.size()) {
            throw new PrivateException(StatusCode.WRONG_INPUT_BOARD_IMAGE_NUM);
        }

        //신규 사진 과 existedURL 사진의 합이 3장을넘으면 에러
        if(newFileList.size() + existedUrlListFromFront.size() > 3){
            throw new PrivateException(StatusCode.WRONG_INPUT_EXISTED_URL_NUM_WITH_VOTE_BOARD_IMAGE_NUM);
        }

        //기존 사진 수 와 existedURL의 수를 비교해서 다르다 => 삭제해야할 사진이 존재한다는 의미
        if (existedBoardImgList.size() != existedUrlListFromFront.size()) {
            //삭제해야 하는 Img List를 받아온다
            List<Img> diffImgList = getDiffImgListByComparingTwoList(existedBoardImgList, existedUrlListFromFront);
            //해당 Img들을 삭제한다. In S3
            List<Img> deletedImgList = awsS3Service.deleteAll(diffImgList);
            //해당 Img들을 삭제한다. In DB (Img Repository)
            if (deletedImgList == null) {
                throw new PrivateException(StatusCode.IMAGE_DELETE_ERROR);
            }
            existedBoardImgList.removeAll(deletedImgList);
        }
        //기존 사진 수 와 existedURL의 수가 동일하다 => 삭제해야할 사진이 없다
        else {
            //수가 동일하지 2개의 데이터가 동일한지는 모르므로 데이터 비교를 해야함
            List<Img> diffImgList = getDiffImgListByComparingTwoList(existedBoardImgList, existedUrlListFromFront);
            //다른 데이터가 있다는 것은 existedURL 값이 이상하다는 것
            if(!diffImgList.isEmpty()){
                throw new PrivateException(StatusCode.WRONG_INPUT_EXISTED_URL);
            }
        }

        //신규사진이 존재한다 => 업로드 해야할 사진이 존재한다는 의미
        if(newFileList.size() != 0){
            //해당 Img들을 추가한다. In S3
            List<String> savedImgPaths = awsS3Service.uploadFiles(newFileList);
            for (String savedImgPath : savedImgPaths) {
                //해당 Img들을 추가한다. In DB (Img Repository)
                existedBoardImgList.add(Img.of(findedBoard, savedImgPath));
            }
        }

        //Board 내용 Update
        findedBoard.update(requestDto);

        List<String> imgPathList = convertBoardImgListToImgPathList(existedBoardImgList);

        return BoardResponseDto.DetailResponse.builder()
                .boardPostId(findedBoard.getBoardPostId())
                .memberId(memberId)
                .createAt(findedBoard.getCreatedAt())
                .title(findedBoard.getTitle())
                .contents(findedBoard.getContents())
                .category(findedBoard.getCategory())
                .imgUrl(imgPathList)
                .build();
    }

    private List<String> convertBoardImgListToImgPathList(List<Img> existedBoardImgList) {
        List<String> rtValList = new ArrayList<String>(3);
        for (Img img : existedBoardImgList) {
            rtValList.add(img.getImgUrl());
        }

        return rtValList;
    }

    private List<Img> getDiffImgListByComparingTwoList(List<Img> existedBoardImgList, List<String> existedUrlListFromFront) {
        //기존에 있던 이미지들을 다 삭제하고 새로운 이미지를 쓰려고 함
        if(existedUrlListFromFront.size() == 0){
            return existedBoardImgList;
        }

        List<Img> diffImgList = new ArrayList<>(3);

        //기존 사진 과 existedUrlListFromFront 를 비교하여 다른 URL을 가진 existedBoardImg 객체는 diffImgList 에 추가한다
        // => existedUrlListFromFront 에 없는 URL 을 Img 는 삭제되어야할 Img 이다.
        for (Img img : existedBoardImgList) {
            if (!existedUrlListFromFront.contains(img.getImgUrl())) {
                diffImgList.add(img);
            }
        }
        return diffImgList;
    }

    //이미지 업로드 하는애 2205071800 변경
    @Transactional
    public List<Img> imgModelUpdate(Board board , List<String> savedImgPaths) {

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

    public LikesResponseDto updatePostLikes(Long boardPostId, boolean likes) {
        //like 하려는 boardPostId 가 존재하는지 확인하기
         Board findedBoard = boardRepository.findById(boardPostId).orElseThrow(() -> new PrivateException(StatusCode.NOT_FOUND_POST));

        //Member 가져오기
        String memberId = SecurityUtil.getCurrentMemberId();
        Member findedMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new PrivateException(StatusCode.NOT_FOUND_MEMBER)); //JWT 사용자 MemberId가 존재하지 않음

        //Follow Entity에서 중복체크 필요. 이미 되어있으면 처리되면 X
        Likes findedLikes = likesRepository.findByMemberAndBoard(findedMember, findedBoard)
                .orElse(null);

        LikesResponseDto likesResponseDto = new LikesResponseDto();

        // 1. Likes = true  , findedLikes = 이미 존재   :  아무 처리 X , return = true
        // 2. Likes = false , findedLikes = null       :  아무 처리 X , return = false
        // 3. Likes = true  , findedLikes = null        :  추가
        // 4. Likes = false , findedLikes = 이미 존재  :  삭제

        //없으면 추가하기
        if(likes && findedLikes != null){ //1.
            likesResponseDto.setLikes(true);
        }else if(!likes && findedLikes == null){ //2.
            likesResponseDto.setLikes(false);
        }else if(likes && findedLikes == null){ //3.
            //Follow Table 에 추가하기
            likesRepository.save(Likes.builder()
                    .member(findedMember)
                    .board(findedBoard)
                    .build());
            likesResponseDto.setLikes(true);
        }else if(!likes && findedLikes != null){ //4.
            //Follow Table 에서 삭제
            likesRepository.deleteById(findedLikes.getLikesId());
            likesResponseDto.setLikes(false);
        }

        return likesResponseDto;
    }
}

