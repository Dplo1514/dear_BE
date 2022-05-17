package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.SimpleBoardInfoInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board , Long> {
    List<Board> findAllByOrderByCreatedAtDesc();

    Page<Board> findAllByMemberMemberIdOrderByCreatedAtDesc(String memberId , Pageable pageable);
    List<Board> findAllByMemberMemberIdOrderByCreatedAtDesc(String memberId);

    @Query(nativeQuery = true,
            value = "select board_post_id as postId, title, category, created_at from board b "
            + "union all "
            + "select vote_board_id as postId, title, category, created_at from vote_board v order by created_at desc",
            countQuery = "select * from (" +
                    "select board_post_id as postId, title, category, created_at from board b " +
                    "union all " +
                    "select vote_board_id as postId, title, category, created_at from vote_board v order by created_at desc)"
    )
    Page<SimpleBoardInfoInterface> findAllPost(Pageable pageable);


    @Query(nativeQuery = true,
            value = "select board_post_id as postId, title, category, created_at from board b where category = :category order by created_at desc",
            countQuery = "select * from (select board_post_id as postId, title, category, created_at from board b where category = :category order by created_at desc)"
    )
    Page<SimpleBoardInfoInterface> findAllPostWithCategory(String category, Pageable pageable);
}