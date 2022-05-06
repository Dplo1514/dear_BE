package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.CommentLikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikesRepository extends JpaRepository<CommentLikes , Long> {
    Optional<CommentLikes> findByMemberId (String memberId);
}
