package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.ReviewRequestDto;
import com.sparta.hh99_actualproject.model.RequestTag;
import com.sparta.hh99_actualproject.repository.RequestTagRepository;
import com.sparta.hh99_actualproject.service.validator.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResReviewService {
    private final RequestTagRepository requestTagRepository;
    private final Validator validator;
    private final ScoreService scoreService;
    private final ServiceCommentService serviceCommentService;


    @Transactional
    public void createResReview(ReviewRequestDto reviewRequestDto) {
        //자기 멤버 정보 확인
        String myMemberId = validator.validateMyMemberId();

        //반대편 멤버 정보 확인
        String oppositeMemberId = validator.validateOppositeMemberId(reviewRequestDto);

        //RequestTag는 상대방의 ID로 저장이 된다.
        List<RequestTag> findedRequestTagList = requestTagRepository.findAllByMemberId(oppositeMemberId);

        int[] reqTagArray = new int[3];

        //적절한 ValidRequestTag가 없으면 null로 할당
        RequestTag validRequestTag = findValidRequestTagInList(findedRequestTagList, reviewRequestDto);
        matchReqTagArrayWithValidRequestTag(reqTagArray , validRequestTag);

        int tagCount = scoreService.evaluateResTagCount(reviewRequestDto, reqTagArray);

        float scoreValue = scoreService.getScoreValue(tagCount, reviewRequestDto);

        if(validRequestTag != null)
            updateRequestTag(validRequestTag,reqTagArray); //기존 데이터가 있는 경우 ( 기존 데이터 업데이트 )
        else
            createRequestTag(oppositeMemberId, reviewRequestDto, reqTagArray); // 기존 데이터가 null 인 경우 ( 신규 데이터 생성 )

        //서비스 후기 저장
        serviceCommentService.save(myMemberId, reviewRequestDto);

        //점수 계산 해서  Score 적용 필요
        scoreService.calculateMemberScore(oppositeMemberId, scoreValue, ScoreType.RESPONSE_CHAT);
    }

    private RequestTag findValidRequestTagInList(List<RequestTag> findedRequestTagList, ReviewRequestDto reqReviewRequestDto) {
        //RequestTagList 에서 불러온 값들중에서 좋아요/나빠요가 매칭되는 RequestTag 를 return
        for (RequestTag findedRequestTag : findedRequestTagList) {
            if(reqReviewRequestDto.isTagLike() == findedRequestTag.isLike()){
                return findedRequestTag;
            }
        }
        return null;
    }

    private void matchReqTagArrayWithValidRequestTag(int[] reqTagArray, RequestTag requestTag) {
        //기존 값이 있는 경우
        if(requestTag != null) {
            reqTagArray[0] = requestTag.getReqTag1Num();
            reqTagArray[1] = requestTag.getReqTag2Num();
            reqTagArray[2] = requestTag.getReqTag3Num();
        }
    }

    private void updateRequestTag(RequestTag validRequestTag, int[] reqTagArray) {
        validRequestTag.setReqTag1Num(reqTagArray[0]);
        validRequestTag.setReqTag2Num(reqTagArray[1]);
        validRequestTag.setReqTag3Num(reqTagArray[2]);
    }

    private void createRequestTag(String memberId, ReviewRequestDto reviewRequestDto, int[] reqTagArray) {
        //Tag 저장 => RequestTag에 저장을 해야함
        RequestTag requestTag = RequestTag.builder()
                .memberId(memberId)
                .isLike(reviewRequestDto.isTagLike())
                .reqTag1Num(reqTagArray[0])
                .reqTag2Num(reqTagArray[1])
                .reqTag3Num(reqTagArray[2])
                .build();

        requestTagRepository.save(requestTag);
    }
}
