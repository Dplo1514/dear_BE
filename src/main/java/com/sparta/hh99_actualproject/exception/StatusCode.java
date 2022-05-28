package com.sparta.hh99_actualproject.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StatusCode {
    OK(HttpStatus.OK, "0", "정상"),

    SIGNUP_MEMBER_ID_FORM_ERROR(HttpStatus.BAD_REQUEST, "100", "memberId 형식을 맞춰주세요"),
    SIGNUP_MEMBER_ID_DUPLICATE_ERROR(HttpStatus.BAD_REQUEST, "101", "memberId 가 중복됩니다"),
    SIGNUP_NICKNAME_FORM_ERROR(HttpStatus.BAD_REQUEST, "102", "nickname 형식을 맞춰주세요"),
    SIGNUP_NICKNAME_DUPLICATE_ERROR(HttpStatus.BAD_REQUEST, "103", "nickname 이 중복됩니다"),
    SIGNUP_PASSWORD_CHECK_ERROR(HttpStatus.BAD_REQUEST, "104", "password 와 passwordCheck 가 다릅니다"),
    SIGNUP_PASSWORD_FORM_ERROR(HttpStatus.BAD_REQUEST, "105", "password 형식을 맞춰주세요"),

    LOGIN_MEMBER_ID_FAIL(HttpStatus.NOT_FOUND, "110", "해당 하는 memberId 가 없습니다"),

    LOGIN_PASSWORD_FAIL(HttpStatus.BAD_REQUEST, "111", "Password가 틀렸습니다."),
    LOGIN_WRONG_SIGNATURE_JWT_TOKEN(HttpStatus.BAD_REQUEST, "112", "잘못된 JWT 서명입니다."),
    LOGIN_EXPIRED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "113", "만료된 JWT 토큰입니다."),
    LOGIN_NOT_SUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "114", "지원되지 않는 JWT 토큰입니다."),
    LOGIN_WRONG_FORM_JWT_TOKEN(HttpStatus.BAD_REQUEST, "115", "JWT 토큰이 잘못되었습니다."),

    LOGIN_MEMBER_REQUIRED_INFORMATION_FAIL(HttpStatus.BAD_REQUEST, "116", "필수 입력 정보를 입력 후 시도해주세요"),

    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "200", "해당 피드가 존재하지 않습니다"),
    WRONG_ACCESS_POST_UPDATE(HttpStatus.BAD_REQUEST, "201", "본인 피드만 수정할 수 있습니다"),
    WRONG_ACCESS_POST_DELETE(HttpStatus.BAD_REQUEST, "202", "본인 피드만 삭제할 수 있습니다"),
    WRONG_INPUT_CONTENT(HttpStatus.BAD_REQUEST, "203", "내용을 입력해주세요"),
    WRONG_INPUT_BOARD_IMAGE_NUM(HttpStatus.BAD_REQUEST, "204", "이미지는 총 3장까지만 등록 가능합니다"),
    WRONG_INPUT_VOTE_SELECTION(HttpStatus.BAD_REQUEST, "205", "SelectionNum 을 다시 한번 확인해주세요"),
    WRONG_INPUT_VOTE_BOARD_IMAGE_NUM(HttpStatus.BAD_REQUEST, "206", "이미지는 업로드 하지 않거나 2장이 모두 존재해야 합니다"),
    WRONG_INPUT_EXISTED_URL(HttpStatus.BAD_REQUEST, "207", "existedURL로 제공해주신 항목이 기존에 존재하는 IMG URL 과 맞지않습니다"),
    WRONG_INPUT_EXISTED_URL_NUM_WITH_VOTE_BOARD_IMAGE_NUM(HttpStatus.BAD_REQUEST, "208", "기존에 있던 URL 수 와 제공해주신 existedURL의 수가 맞지않습니다"),

    IMAGE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "210", "이미지 업로드에 실패했습니다"),
    WRONG_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "211", "지원하지 않는 파일 형식입니다"),
    IMAGE_DELETE_ERROR(HttpStatus.BAD_REQUEST, "212", "이미지 삭제에 실패했습니다."),

    PAGING_ERROR(HttpStatus.BAD_REQUEST, "220", "모든 요소가 필요합니다"),
    PAGING_NUM_ERROR(HttpStatus.BAD_REQUEST, "221", "페이지는 1부터 시작됩니다"),

    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "300", "해당 댓글이 존재하지 않습니다"),
    WRONG_ACCESS_COMMENT_UPDATE(HttpStatus.BAD_REQUEST, "301", "본인 댓글만 수정할 수 있습니다"),
    WRONG_ACCESS_COMMENT_DELETE(HttpStatus.BAD_REQUEST, "302", "본인 댓글만 삭제할 수 있습니다"),
    WRONG_INPUT_COMMENT(HttpStatus.BAD_REQUEST, "303", "댓글을 입력해주세요"),
    WRONG_ACCESS_COMMENTLIKES(HttpStatus.BAD_REQUEST, "304", "댓글 채택 권한이 없는 사용자입니다."),
    NOT_FOUND_COMMENT_LIKE(HttpStatus.BAD_REQUEST, "305", "해당 댓글 채택내용이 존재하지 않습니다."),
    WRONG_ACCESS_COMMENT_UPDATE_LIKE(HttpStatus.BAD_REQUEST, "306", "채택된 댓글의 수정이 불가능합니다."),
    WRONG_ACCESS_CHECK_SELF_COMMENTLIKES(HttpStatus.BAD_REQUEST, "307", "자신의 댓글은 채택이 불가능합니다."),
    WRONG_ACCESS_CHECK_SELF_COMMENTLIKES_IP(HttpStatus.BAD_REQUEST, "308", "(IP) 자신의 댓글은 채택이 불가능합니다. "),



    NULL_INPUT_CHAT_REQUEST(HttpStatus.BAD_REQUEST, "600", "필수 입력항목중 미입력 항목이 존재합니다."),
    NULL_INPUT_CHAT_RESPONSE(HttpStatus.BAD_REQUEST, "601", "필수 입력항목중 미입력 항목이 존재합니다."),
    NOT_FOUND_CHAT_ROOM(HttpStatus.BAD_REQUEST, "602", "채팅방을 찾을 수 없습니다."),
    WRONG_REQUEST_CHAT_ROOM(HttpStatus.BAD_REQUEST, "603", "최대 채팅시간 연장 횟수를 초과했습니다."),
    WRONG_ACCESS_CHAT_REWARD(HttpStatus.BAD_REQUEST, "604", "리워드 부족으로 채팅의 시작이 불가능합니다."),
    WRONG_ACCESS_CHAT_MATCH_SAME_USER(HttpStatus.BAD_REQUEST, "605", "같은 유저끼리는 매칭이 불가능합니다."),

    WRONG_ACCESS_CHAT_MATCH_SAME_IP(HttpStatus.BAD_REQUEST, "605", "동일 IP USER의 채팅이 불가능합니다."),

    WRONG_ACCESS_CHAT_MATCH_CATEGORY(HttpStatus.BAD_REQUEST, "605", "잘못된 카테고리가 입력되었습니다."),

    WRONG_ACCESS_CHAT_MATCH_GENDER(HttpStatus.BAD_REQUEST, "605", "잘못된 성별이 입력되었습니다."),


    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "400", "해당 회원을 찾을 수 없습니다"),
    NOT_FOUND_JWT_TOKEN(HttpStatus.NOT_FOUND, "500", "JWT 이 존재하지 않습니다. 다시 확인해주세요."),

    IS_NOT_MATCHING_REQUEST_REVIEW(HttpStatus.NOT_FOUND, "700", "isRequestReview 부분을 확인해주세요"),
    IS_NOT_ALLOW_INSERT_MEMBER_ID(HttpStatus.NOT_FOUND, "701", "memberId로 본인 Id를 넣을 수 없습니다"),
    IS_NOT_ALLOW_TAG_SELECT_LIST_SIZE(HttpStatus.NOT_FOUND, "702", "tagSelectList 의 수를 확인해주세요 Request : 5 , Response : 3"),
    WRONG_INPUT_SERVICE_COMMENT(HttpStatus.NOT_FOUND, "703", "ServiceComment의 내용은 20자 이하만 가능합니다"),

    NOT_FOUND_SCORE(HttpStatus.NOT_FOUND, "704", "회원의 획득한 점수가 없습니다."),

    NOT_FOUND_MESSAGE(HttpStatus.NOT_FOUND, "800", "해당 메시지를 찾을 수 없습니다."),
    NULL_INPUT_MESSAGE_ERROR(HttpStatus.NOT_FOUND, "801", "메시지 필수항목이 존재하지 않습니다."),



    NULL_INPUT_ERROR(HttpStatus.NOT_FOUND, "990", "Null 값이 들어왔습니다"),
    NOT_FOUND_AUTHORIZATION_IN_SECURITY_CONTEXT(HttpStatus.INTERNAL_SERVER_ERROR, "998", "Security Context에 인증 정보가 없습니다."),
    INTERNAL_SERVER_ERROR_PLZ_CHECK(HttpStatus.INTERNAL_SERVER_ERROR, "999", "알수없는 서버 내부 에러 발생. 서버 담당자에게 알려주세요.");


    private final HttpStatus httpStatus;
    private final String statusCode;
    private final String statusMsg;

    StatusCode(HttpStatus httpStatus, String statusCode, String statusMsg) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }
}