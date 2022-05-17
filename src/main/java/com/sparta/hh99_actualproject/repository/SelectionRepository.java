package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.Selection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SelectionRepository extends JpaRepository<Selection,Long> {
    Optional<Selection> findByVoteBoardIdAndMemberId(Long voteBoardId, String memberId);
    List<Selection> findAllByVoteBoardIdAndSelectionNum(Long voteBoardId, int selectionNum);

    @Query(value = "SELECT vote_board_Id " +
            "FROM selection " +
            "group by vote_board_Id " +
            "order by count(*) desc " +
            "LIMIT 3"
            , nativeQuery = true)
    List<Long> findTop3VoteBoardIdOrderByTotalVoteNum();

    void deleteAllByVoteBoardId(Long voteBoardId);
}
