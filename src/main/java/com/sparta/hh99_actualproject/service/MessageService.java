package com.sparta.hh99_actualproject.service;
import com.sparta.hh99_actualproject.dto.MessageDto.MessageDetailResponseDto;
import com.sparta.hh99_actualproject.dto.MessageDto.MessageRequestDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.model.Message;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.repository.MessageRepository;
import com.sparta.hh99_actualproject.service.validator.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final Validator validator;
    public MessageDetailResponseDto getMessageDetail(Long messageId) {

        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MESSAGE));

        return MessageDetailResponseDto.builder()
                .reqUser(message.getReqMemberNickname())
                .resUser(message.getResMemberNickname())
                .createdAt(String.valueOf(message.getCreatedAt()))
                .message(message.getMessage())
                .build();
    }

    public void sendMessage(MessageRequestDto messageRequestDto) {
        Member resMember = memberRepository.findByNickname(messageRequestDto.getResUser()).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));
        validator.hasNullCheckMessage(messageRequestDto);
        Message message = Message.builder()
                .member(resMember)
                .reqMemberNickname(messageRequestDto.getReqUser())
                .resMemberNickname(resMember.getNickname())
                .message(messageRequestDto.getMessage())
                .build();

        messageRepository.save(message);
    }
}
