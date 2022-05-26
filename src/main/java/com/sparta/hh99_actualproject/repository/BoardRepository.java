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
            value = "SELECT tt.postId, title, category, created_at, likes, comments from (select board_post_id as postId, title, category, created_at from board b "
                    + "union all "
                    + "select vote_board_id as postId, title, category, created_at from vote_board v ) tt "
                    + "left outer join (select board_id as postId,count(*) as likes from likes GROUP by board_id) ll "
                    + "on tt.postId = ll.postId "
                    + "left outer join (select board_id as postId,count(*) as comments from comment GROUP by board_id) cc "
                    + "on tt.postId = cc.postId "
                    + "order by created_at desc",
            countQuery = "select * from ("
                    + "SELECT tt.postId, title, category, created_at, likes, comments from (select board_post_id as postId, title, category, created_at from board b "
                    + "union all "
                    + "select vote_board_id as postId, title, category, created_at from vote_board v ) tt "
                    + "left outer join (select board_id as postId,count(*) as likes from likes GROUP by board_id) ll "
                    + "on tt.postId = ll.postId "
                    + "left outer join (select board_id as postId,count(*) as comments from comment GROUP by board_id) cc "
                    + "on tt.postId = cc.postId "
                    + "order by created_at desc "
                    + ") as aaa"
    )
    Page<SimpleBoardInfoInterface> findAllPost(Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT tt.postId, title, category, created_at, likes, comments from (select board_post_id as postId, title, category, created_at from board b where b.member_id = :memberId "
                    + "union all "
                    + "select vote_board_id as postId, title, category, created_at from vote_board v where v.member_id = :memberId ) tt "
                    + "left outer join (select board_id as postId,count(*) as likes from likes GROUP by board_id) ll "
                    + "on tt.postId = ll.postId "
                    + "left outer join (select board_id as postId,count(*) as comments from comment GROUP by board_id) cc "
                    + "on tt.postId = cc.postId "
                    + "order by created_at desc",
            countQuery = "select * from ("
                    + "SELECT tt.postId, title, category, created_at, likes, comments from (select board_post_id as postId, title, category, created_at from board b where b.member_id = :memberId "
                    + "union all "
                    + "select vote_board_id as postId, title, category, created_at from vote_board v where v.member_id = :memberId ) tt "
                    + "left outer join (select board_id as postId,count(*) as likes from likes GROUP by board_id) ll "
                    + "on tt.postId = ll.postId "
                    + "left outer join (select board_id as postId,count(*) as comments from comment GROUP by board_id) cc "
                    + "on tt.postId = cc.postId "
                    + "order by created_at desc "
                    + ") as aaa"
    )
    Page<SimpleBoardInfoInterface> findAllPostByMemberId(String memberId,Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT tt.postId, title, category, created_at, likes, comments from (select board_post_id as postId, title, category, created_at from board b where b.category = :category) tt "
                    + "left outer join (select board_id as postId,count(*) as likes from likes l GROUP by l.board_id) ll "
                    + "on tt.postId = ll.postId "
                    + "left outer join (select board_id as postId,count(*) as comments from comment c GROUP by c.board_id) cc "
                    + "on tt.postId = cc.postId "
                    + "order by created_at desc",
            countQuery = "select * from ("
                    + "SELECT tt.postId, title, category, created_at, likes, comments from (select board_post_id as postId, title, category, created_at from board b where b.category = :category) tt "
                    + "left outer join (select board_id as postId,count(*) as likes from likes l GROUP by l.board_id) ll "
                    + "on tt.postId = ll.postId "
                    + "left outer join (select board_id as postId,count(*) as comments from comment c GROUP by c.board_id) cc "
                    + "on tt.postId = cc.postId "
                    + "order by created_at desc "
                    + ") as aaa"
    )
    Page<SimpleBoardInfoInterface> findAllPostWithCategory(String category, Pageable pageable);
}