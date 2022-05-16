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

    @Query(nativeQuery = true,
            value = "select board_Post_Id as postId, title, category, created_at from Board b "
            + "union all "
            + "select vote_Board_Id as postId, title, category, created_at from Vote_Board v order by created_at desc",
            countQuery = "select * from (" +
                    "select board_Post_Id as postId, title, category, created_at from Board b " +
                    "union all " +
                    "select vote_Board_Id as postId, title, category, created_at from Vote_Board v order by created_at desc)"
    )
    Page<SimpleBoardInfoInterface> findAllPost(Pageable pageable);


    @Query(nativeQuery = true,
            value = "select board_Post_Id as postId, title, category, created_At from Board b where category = :category order by created_at desc",
            countQuery = "select * from (select board_Post_Id as postId, title, category, created_At from Board b where category = :category order by created_at desc)"
    )
    Page<SimpleBoardInfoInterface> findAllPostWithCategory(String category, Pageable pageable);
}