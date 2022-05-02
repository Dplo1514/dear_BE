package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.VoteBoard;
import com.sparta.hh99_actualproject.model.VoteContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteContentRepository extends JpaRepository<VoteContent,Long> {
}
