package com.pickgo.domain.admin.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pickgo.domain.member.dto.MemberSimpleResponse;
import com.pickgo.domain.member.service.MemberService;
import com.pickgo.global.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

	private final MemberService memberService;

	public PageResponse<MemberSimpleResponse> getPagedMembers(Pageable pageable) {
		return memberService.getPagedMembers(pageable);
	}
}
