package com.pickgo.example.controller;

import static com.pickgo.global.response.RsCode.*;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pickgo.example.dto.ExampleCreateRequest;
import com.pickgo.example.dto.ExampleDetailResponse;
import com.pickgo.example.dto.ExampleSimpleResponse;
import com.pickgo.example.dto.ExampleUpdateRequest;
import com.pickgo.example.entity.ExampleType;
import com.pickgo.example.service.ExampleService;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.response.RsData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/examples")
@RequiredArgsConstructor
@Tag(name = "Example API", description = "Example API 엔드포인트")
public class ExampleController {

	private final ExampleService exampleService;

	@Operation(summary = "Example 생성")
	@PostMapping
	public RsData<ExampleDetailResponse> create(@RequestBody @Valid ExampleCreateRequest request) {
		ExampleDetailResponse response = exampleService.create(request);
		return RsData.from(CREATED, response);
	}

	@Operation(summary = "Example 수정")
	@PutMapping("/{id}")
	public RsData<?> update(
		@PathVariable("id") Long id,
		@RequestBody @Valid ExampleUpdateRequest request) {
		ExampleDetailResponse response = exampleService.update(id, request);
		return RsData.from(SUCCESS, response);
	}

	@Operation(summary = "Example 페이징 조회")
	@GetMapping
	public RsData<PageResponse<ExampleSimpleResponse>> list(
		@RequestParam("type") ExampleType type,
		@ParameterObject @PageableDefault(sort = "id") Pageable pageable
	) {
		PageResponse<ExampleSimpleResponse> response = exampleService.getPagedExamples(pageable, type);
		return RsData.from(SUCCESS, response);
	}

	@Operation(summary = "Example 전체 조회")
	@GetMapping("/all")
	public RsData<List<ExampleSimpleResponse>> listAll() {
		List<ExampleSimpleResponse> responses = exampleService.getSimpleList();
		return RsData.from(SUCCESS, responses);
	}

	@Operation(summary = "Example id로 조회")
	@GetMapping("/{id}")
	public RsData<ExampleDetailResponse> get(@PathVariable("id") Long id) {
		ExampleDetailResponse response = exampleService.getDetail(id);
		return RsData.from(SUCCESS, response);
	}
}
