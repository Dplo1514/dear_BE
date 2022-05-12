package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    Optional<List<Comment>> findAllByBoardOrderByCreatedAtDesc(Board board);
}
