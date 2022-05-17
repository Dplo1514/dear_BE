package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score,Long> {

    Optional<Score> findByMemberId(String memberId);
    List<Score> findAllByOrderByScoreDesc();
}
