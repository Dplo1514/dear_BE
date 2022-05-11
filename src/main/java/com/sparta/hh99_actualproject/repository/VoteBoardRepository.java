package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.VoteBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteBoardRepository extends JpaRepository<VoteBoard,Long> {
    List<VoteBoard> findAllByOrderByCreatedAtDesc();

}
