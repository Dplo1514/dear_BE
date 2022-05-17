package com.sparta.hh99_actualproject.model;

import java.time.LocalDateTime;

public interface SimpleBoardInfoInterface{
    Long getPostId();

    String getTitle();

    String getCategory();

    LocalDateTime getCreated_at();
}
