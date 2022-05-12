package com.sparta.hh99_actualproject.service;

import lombok.Getter;

@Getter
public enum ScoreType {
    REQUEST_CHAT,
    RESPONSE_CHAT,
    COMMENT_SELECTION,
    COMMENT_SELECTION_TO_CANCEL;
}