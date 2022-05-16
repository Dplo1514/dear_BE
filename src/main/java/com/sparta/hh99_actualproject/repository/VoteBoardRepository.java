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

    @Query(nativeQuery = true,
            value = "select vote_Board_Id as postId, title, category, created_At from Vote_Board v order by created_at desc",
            countQuery = "select * from (select vote_Board_Id as postId, title, category, created_At from Vote_Board v order by created_at desc)"
    )
    Page<SimpleBoardInfoInterface> findAllPostWithVote(Pageable pageable);

}
