package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.ServiceComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceCommentRepository extends JpaRepository<ServiceComment,Long> {
    List<ServiceComment> findAllByOrderByCreatedAtDesc();
}
