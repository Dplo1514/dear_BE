package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.ReviewRequestDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.ResponseTag;
import com.sparta.hh99_actualproject.repository.ResponseTagRepository;
import com.sparta.hh99_actualproject.service.validator.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReqReviewService {
    private final ResponseTagRepository responseTagRepository;
    private final Validator validator;
    private final ScoreService scoreService;
    private final ServiceCommentService serviceCommentService;


    //후기저장
    @Transactional
    public void createReqReview(ReviewRequestDto reviewRequestDto) {
        //자기 멤버 정보 확인
        String myMemberId = validator.validateMyMemberId();

        //반대편 멤버 정보 확인
        String oppositeMemberId = validator.validateOppositeMemberId(reviewRequestDto);

        //자기 ID가 반대편 멤버에 있는 경우 Err
        if (myMemberId.equals(oppositeMemberId))
            throw new PrivateException(StatusCode.IS_NOT_ALLOW_INSERT_MEMBER_ID);

        //ResponseTag는 상대방의 ID로 저장이 된다.
        List<ResponseTag> findedResponseTagList = responseTagRepository.findAllByMemberId(oppositeMemberId);

        int[] resTagArray = new int[5];

        //적절한 ValidResponseTag가 없으면 null로 할당
        ResponseTag validResponseTag = findValidResponseTagInList(findedResponseTagList, reviewRequestDto);
        matchResTagArrayWithValidResponseTag(resTagArray, validResponseTag);

        int tagCount = scoreService.evaluateResTagCount(reviewRequestDto, resTagArray);

        float scoreValue = scoreService.getScoreValue(tagCount, reviewRequestDto);

        if (validResponseTag != null)
            updateResponseTag(validResponseTag, resTagArray); //기존 데이터가 있는 경우 ( 기존 데이터 업데이트 )
        else
            createResponseTag(oppositeMemberId, reviewRequestDto); // 기존 데이터가 null 인 경우 ( 신규 데이터 생성 )

        //서비스 후기 저장
        serviceCommentService.save(myMemberId, reviewRequestDto);

        //점수 계산 해서  Score 적용 필요
        scoreService.calculateMemberScore(oppositeMemberId, scoreValue, ScoreType.REQUEST_CHAT);
    }

    private void createResponseTag(String memberId, ReviewRequestDto reviewRequestDto) {
        //Tag 저장 => ResponseTag에 저장을 해야함
        ResponseTag responseTag = ResponseTag.builder()
                .memberId(memberId)
                .isLike(reviewRequestDto.isTagLike())
                .resTag1Num(0)
                .resTag2Num(0)
                .resTag3Num(0)
                .resTag4Num(0)
                .resTag5Num(0)
                .build();

        responseTagRepository.save(responseTag);
    }

    private void updateResponseTag(ResponseTag validResponseTag, int[] resTagArray) {
        validResponseTag.setResTag1Num(resTagArray[0]);
        validResponseTag.setResTag2Num(resTagArray[1]);
        validResponseTag.setResTag3Num(resTagArray[2]);
        validResponseTag.setResTag4Num(resTagArray[3]);
        validResponseTag.setResTag5Num(resTagArray[4]);
    }

    private ResponseTag findValidResponseTagInList(List<ResponseTag> findedResponseTagList, ReviewRequestDto reqReviewRequestDto) {
        //responseTagList 에서 불러온 값들중에서 좋아요/나빠요가 매칭되는 responseTag 를 return
        for (ResponseTag findedResponseTag : findedResponseTagList) {
            if (reqReviewRequestDto.isTagLike() == findedResponseTag.isLike()) {
                return findedResponseTag;
            }
        }
        return null;
    }

    private void matchResTagArrayWithValidResponseTag(int[] resTagArray, ResponseTag responseTag) {
        //기존 값이 있는 경우
        if (responseTag != null) {
            resTagArray[0] = responseTag.getResTag1Num();
            resTagArray[1] = responseTag.getResTag2Num();
            resTagArray[2] = responseTag.getResTag3Num();
            resTagArray[3] = responseTag.getResTag4Num();
            resTagArray[4] = responseTag.getResTag5Num();
        }
    }
}
