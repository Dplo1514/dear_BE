package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.SimpleBoardInfoInterface;
import com.sparta.hh99_actualproject.model.VoteBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteBoardRepository extends JpaRepository<VoteBoard,Long> {
    List<VoteBoard> findAllByOrderByCreatedAtDesc();
    List<VoteBoard> findAllByMemberMemberIdOrderByCreatedAtDesc(String memberId);
    @Query(nativeQuery = true,
            value = "select vote_board_id as postId, title, category, created_at from vote_board v order by created_at desc",
            countQuery = "select * from (select vote_board_id as postId, title, category, created_at from vote_board v order by created_at desc) as ab"
    )
    Page<SimpleBoardInfoInterface> findAllPostWithVote(Pageable pageable);

}
