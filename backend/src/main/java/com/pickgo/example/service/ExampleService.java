package com.pickgo.example.service;

import static com.pickgo.global.response.RsCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pickgo.example.dto.ExampleCreateRequest;
import com.pickgo.example.dto.ExampleDetailResponse;
import com.pickgo.example.dto.ExampleSimpleResponse;
import com.pickgo.example.dto.ExampleUpdateRequest;
import com.pickgo.example.entity.Example;
import com.pickgo.example.entity.ExampleType;
import com.pickgo.example.repository.ExampleRepository;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExampleService {

	private final ExampleRepository exampleRepository;

	@Transactional
	public ExampleDetailResponse create(ExampleCreateRequest request) {
		Example example = exampleRepository.save(request.toEntity());
		return ExampleDetailResponse.from(example);
	}

	@Transactional
	public ExampleDetailResponse update(Long id, ExampleUpdateRequest request) {
		Example example = getEntity(id);
		example.updateExample(request.title(), request.body(), request.type());
		return ExampleDetailResponse.from(example);
	}

	@Transactional(readOnly = true)
	public PageResponse<ExampleSimpleResponse> getPagedExamples(Pageable pageable, ExampleType type) {
		Page<Example> examples = exampleRepository.findAllByType(pageable, type);
		return PageResponse.from(examples, ExampleSimpleResponse::from);
	}

	@Transactional(readOnly = true)
	public List<ExampleSimpleResponse> getSimpleList() {
		return exampleRepository.findAll().stream().map(ExampleSimpleResponse::from).toList();
	}

	@Transactional(readOnly = true)
	public ExampleDetailResponse getDetail(Long id) {
		Example example = getEntity(id);
		return ExampleDetailResponse.from(example);
	}

	private Example getEntity(Long id) {
		return exampleRepository.findById(id).orElseThrow(() -> new BusinessException(NOT_FOUND));
	}
}
