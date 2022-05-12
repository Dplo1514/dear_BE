package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.ReviewRequestDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.ServiceComment;
import com.sparta.hh99_actualproject.repository.ServiceCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceCommentService {
    private final ServiceCommentRepository serviceCommentRepository;

    public void save(String myMemberId , ReviewRequestDto reviewRequestDto){
        String serviceComment = reviewRequestDto.getServiceComment();
        if(serviceComment == null || serviceComment.trim().equals("")){
            return;
        }

        if(serviceComment.trim().length() > 20){
            throw new PrivateException(StatusCode.WRONG_INPUT_SERVICE_COMMENT);
        }
        serviceCommentRepository.save(ServiceComment.of(myMemberId, reviewRequestDto.getServiceComment()));
    }
}
