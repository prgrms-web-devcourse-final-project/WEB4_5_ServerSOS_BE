package com.pickgo.member.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pickgo.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, UUID> {
	Optional<Member> findByEmail(String email);
}
