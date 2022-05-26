package com.sparta.hh99_actualproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.hh99_actualproject.dto.*;
import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.FollowService;
import com.sparta.hh99_actualproject.service.KakaoUserService;
import com.sparta.hh99_actualproject.service.MemberService;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final KakaoUserService kakaoUserService;

    private final FollowService followService;

    @PostMapping("/user/signup")
    public ResponseEntity<PrivateResponseBody> signup(@RequestBody MemberRequestDto memberRequestDto) {

        boolean rtval = memberService.signup(memberRequestDto);

        return rtval
                ? new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK), HttpStatus.OK)
                : new ResponseEntity<>(new PrivateResponseBody(StatusCode.INTERNAL_SERVER_ERROR_PLZ_CHECK), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/user/memberIdCheck")
    public ResponseEntity<PrivateResponseBody> checkMemberId(@RequestBody CheckRequestDto CheckRequestDto) {
        memberService.checkMemberId(CheckRequestDto.getMemberId());

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK), HttpStatus.OK);
    }

    @PostMapping("/user/nicknameCheck")
    public ResponseEntity<PrivateResponseBody> checkNickname(@RequestBody CheckRequestDto CheckRequestDto) {
        memberService.checkNickname(CheckRequestDto.getNickname());

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK), HttpStatus.OK);
    }

    @PostMapping("/user/login")
    public ResponseEntity<PrivateResponseBody> login(@RequestBody MemberRequestDto memberRequestDto) {

        TokenDto tokenDto = memberService.login(memberRequestDto);

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,tokenDto), HttpStatus.OK);
    }

    @PostMapping("/user/info")
    public ResponseEntity<PrivateResponseBody> updateMemberInfo(@RequestBody EssentialInfoRequestDto essentialInfoRequestDto) {

        TokenDto tokenDto = memberService.updateMemberInfo(essentialInfoRequestDto);

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,tokenDto), HttpStatus.OK);
    }

    @PostMapping("/user/{memberId}/follow")
    public ResponseEntity<PrivateResponseBody> followMember(@PathVariable("memberId") String followMemberId , @RequestParam boolean follow) {

        FollowResponseDto followResponseDto = followService.followMember(followMemberId,follow);

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,followResponseDto), HttpStatus.OK);
    }

    @GetMapping("/user/kakao/callback")
    public ResponseEntity<PrivateResponseBody> kakaoLogin(@RequestParam(value = "code") String authorityCode) throws JsonProcessingException {
        TokenDto tokenDto = kakaoUserService.kakaoLogin(authorityCode);

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,tokenDto), HttpStatus.OK);
    }

    @GetMapping("/user/info/profile")
    public ResponseEntity<PrivateResponseBody> getUserProfile(){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,memberService.getMemberProfile()), HttpStatus.OK);
    }

    @GetMapping("/user/info/chatHistory")
    public ResponseEntity<PrivateResponseBody> getUserChatHisory(){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,memberService.getMemberChatHistory()), HttpStatus.OK);
    }

    @GetMapping("/user/info/message/{page}")
    public ResponseEntity<PrivateResponseBody> getUserMessage(@PathVariable int page){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,memberService.getMemberMessage(page)), HttpStatus.OK);
    }

    @GetMapping("/user/info/follow/{page}")
    public ResponseEntity<PrivateResponseBody> getUserFollow(@PathVariable int page){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,memberService.getMemberFollow(page)), HttpStatus.OK);
    }

    @GetMapping("/user/info/board/{page}")
    public ResponseEntity<PrivateResponseBody> getUserBoard(@PathVariable int page){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,memberService.getMemberBoard(page)), HttpStatus.OK);
    }

    @GetMapping("/user/info/reward")
    public ResponseEntity<PrivateResponseBody> getUserReward(){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,memberService.getReward()), HttpStatus.OK);
    }


}