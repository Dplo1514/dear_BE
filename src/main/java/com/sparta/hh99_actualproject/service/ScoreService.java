package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.ReviewRequestDto;
import com.sparta.hh99_actualproject.model.Score;
import com.sparta.hh99_actualproject.repository.ScoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ScoreService {
    private final ScoreRepository scoreRepository;

    public int evaluateResTagCount(ReviewRequestDto reviewRequestDto, int[] tagArray) {
        int tagCount = 0;
        List<Boolean> tagSelectList = reviewRequestDto.getTagSelectList();

        for (int i = 0; i < tagSelectList.size(); i++) {
            if (tagSelectList.get(i)) {
                tagCount++;
                tagArray[i] += 1;
            }
        }
        return tagCount;
    }

    public float getScoreValue(int tagCount , ReviewRequestDto reviewRequestDto ) {
        //좋아요는 양수로 나감 (배율 0.2배) , 싫어요는 음수로 나감 (배율 0.5배)
        return reviewRequestDto.isTagLike() ? (tagCount * 0.2f) : (tagCount * -0.5f);
    }

    public void calculateMemberScore(String memberId, float scoreValue, ScoreType scoreType) {
        float oppositeMemberScore = 36.5f; //초기 점수
        int oppositeMemberResponseChatCount = 0;
        int oppositeMemberRequestChatCount = 0;
        int oppositeMemberCommentSelectionCount = 0;
        //1. 기존 유저의 Score 가져옴
        Score oppositeMemberScoreModel = scoreRepository.findByMemberId(memberId)
                .orElse(null);

        //기존에 저장된게 없으므로 Table 생성이 필요 ,
        //생성된게 있으면 Get해서 기존 점수 가져와서 사용 후 새로운 점수를 적용해서 업데이트 필요
        if (oppositeMemberScoreModel == null) {
            oppositeMemberScoreModel = scoreRepository.save(Score.of(memberId, oppositeMemberScore));
        }

        //oppositeMemberScore 점수 계산하기
        oppositeMemberScore = oppositeMemberScoreModel.getScore();
        oppositeMemberScore += scoreValue;

        //※ 마음 온도의 최대 상한은 100도 이다.
        if (oppositeMemberScore > 100f) {
            oppositeMemberScore = 100f;
        }
        //3. oppositeMemberScore 도 수정 반영 (좋은 점수면 + , 나쁜 점수면 -)
        oppositeMemberScoreModel.setScore(oppositeMemberScore);
        //4. 타입에 맞춰서 개수 추가 ( + 1)
        switch (scoreType) {
            case REQUEST_CHAT:
                oppositeMemberRequestChatCount = oppositeMemberScoreModel.getRequestChatCount();
                oppositeMemberScoreModel.setRequestChatCount(oppositeMemberRequestChatCount + 1);
                break;
            case RESPONSE_CHAT:
                oppositeMemberResponseChatCount = oppositeMemberScoreModel.getResponseChatCount();
                oppositeMemberScoreModel.setResponseChatCount(oppositeMemberResponseChatCount + 1);
                break;
            case COMMENT_SELECTION:
                oppositeMemberCommentSelectionCount = oppositeMemberScoreModel.getCommentSelectionCount();
                oppositeMemberScoreModel.setCommentSelectionCount(oppositeMemberCommentSelectionCount + 1);
                break;
        }
    }
}
