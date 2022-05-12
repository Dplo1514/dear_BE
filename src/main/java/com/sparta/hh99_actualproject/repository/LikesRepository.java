package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.Follow;
import com.sparta.hh99_actualproject.model.Likes;
import com.sparta.hh99_actualproject.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes,Long> {
    void deleteByMemberAndBoardPostId(Member member, Long boardPostId);
    Optional<Likes> findByMemberAndBoardPostId(Member member, Long boardPostId);
}
