package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.ChatExtend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatExtendRepository extends JpaRepository<ChatExtend , Long> {
    Optional<ChatExtend> findByChatRoomChatRoomId (String chatRoomId);
}
