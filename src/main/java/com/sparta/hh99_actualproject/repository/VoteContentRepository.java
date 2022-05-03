package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.VoteBoard;
import com.sparta.hh99_actualproject.model.VoteContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteContentRepository extends JpaRepository<VoteContent,Long> {
    List<VoteContent> findAllByImageTitle(String imageTitle);

}
