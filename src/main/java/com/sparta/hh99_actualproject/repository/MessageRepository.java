package com.sparta.hh99_actualproject.repository;



import com.sparta.hh99_actualproject.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Long> {
    Page<Message> findAllByMemberMemberIdOrderByCreatedAt(String memberId , Pageable pageable);

}
