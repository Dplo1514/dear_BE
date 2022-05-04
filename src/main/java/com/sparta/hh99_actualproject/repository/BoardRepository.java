package com.sparta.hh99_actualproject.repository;

import com.sparta.hh99_actualproject.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board , Long> {

}
