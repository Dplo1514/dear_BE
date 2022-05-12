package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.RequestTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestTagRepository extends JpaRepository<RequestTag,Long> {
    List<RequestTag> findAllByMemberId(String memberId);
}
