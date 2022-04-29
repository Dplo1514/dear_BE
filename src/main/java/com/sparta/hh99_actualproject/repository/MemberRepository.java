package com.sparta.hh99_actualproject.repository;


import com.sparta.hh99_actualproject.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByMemberId(String memberId);
    boolean existsByMemberId(String memberId);
    boolean existsByNickname(String nickname);
    Optional<Member> findByKakaoUserId(String kakaoId);
    Optional<Member> findMemberByNickname(String nickname);
}
