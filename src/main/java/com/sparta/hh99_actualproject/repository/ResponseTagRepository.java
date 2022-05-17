package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.ResponseTag;
import com.sparta.hh99_actualproject.model.ServiceComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseTagRepository extends JpaRepository<ResponseTag,Long> {
    List<ResponseTag> findAllByMemberId(String memberId);
    ResponseTag findByMemberId(String memberId);
}
