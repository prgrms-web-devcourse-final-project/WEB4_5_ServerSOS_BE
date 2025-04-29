package com.pickgo.domain.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pickgo.domain.example.entity.Example;
import com.pickgo.domain.example.entity.ExampleType;

public interface ExampleRepository extends JpaRepository<Example, Long> {
	Page<Example> findAllByType(Pageable pageable, ExampleType type);
}
