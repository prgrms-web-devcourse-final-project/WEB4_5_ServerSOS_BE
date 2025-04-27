package com.pickgo.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pickgo.example.entity.Example;
import com.pickgo.example.entity.ExampleType;

public interface ExampleRepository extends JpaRepository<Example, Long> {
	Page<Example> findAllByType(Pageable pageable, ExampleType type);
}
