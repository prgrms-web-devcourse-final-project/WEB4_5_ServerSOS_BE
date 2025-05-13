package com.pickgo.domain.member.admin.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pickgo.domain.member.member.dto.MemberSimpleResponse;
import com.pickgo.domain.member.member.service.MemberService;
import com.pickgo.global.response.PageResponse;

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
