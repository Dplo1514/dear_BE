package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.Selection;
import com.sparta.hh99_actualproject.model.VoteContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SelectionRepository extends JpaRepository<Selection,Long> {
    List<Selection> findAllByVoteContent(VoteContent voteContent);
}
