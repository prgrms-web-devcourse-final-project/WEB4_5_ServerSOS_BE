package com.pickgo.example.repository;

import com.pickgo.example.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminPostRepository extends JpaRepository<Post, Long> {

}
