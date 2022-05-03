package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.dto.CommentResponseDto;
import com.sparta.hh99_actualproject.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<CommentResponseDto> findAllByPostidOrderByCreatedAtDesc(Long postId);
    CommentResponseDto findByContent(String contetnt);

}
