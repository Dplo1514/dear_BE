package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.Likes;
import com.sparta.hh99_actualproject.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes,Long> {
    void deleteByMemberAndBoard(Member member, Board board);
    void deleteByBoard(Board board);
    Optional<Likes> findByMemberAndBoard(Member member, Board board);
}
