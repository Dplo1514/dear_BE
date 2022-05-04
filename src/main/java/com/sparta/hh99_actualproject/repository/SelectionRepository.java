package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.Selection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SelectionRepository extends JpaRepository<Selection,Long> {
    Optional<Selection> findByVoteBoardIdAndMemberId(Long voteBoardId, String memberId);
}
