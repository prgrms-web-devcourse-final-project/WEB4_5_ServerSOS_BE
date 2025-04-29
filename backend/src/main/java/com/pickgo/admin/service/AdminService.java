package com.pickgo.admin.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pickgo.global.dto.PageResponse;
import com.pickgo.member.dto.MemberSimpleResponse;
import com.pickgo.member.service.MemberService;

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
