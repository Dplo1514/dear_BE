package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.Likes;
import com.sparta.hh99_actualproject.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes,Long> {
    void deleteByMemberAndBoard(Member member, Board board);
    void deleteByBoard(Board board);
    Optional<Likes> findByMemberAndBoard(Member member, Board board);
    List<Likes> findAllByBoard(Board board);

    @Query(value = "SELECT board_id " +
            "FROM likes " +
            "group by board_id " +
            "order by count(*) desc " +
            "Limit 4"
            , nativeQuery = true)
    List<Long> findTop4BoardIdOrderByTotalLike();

}
