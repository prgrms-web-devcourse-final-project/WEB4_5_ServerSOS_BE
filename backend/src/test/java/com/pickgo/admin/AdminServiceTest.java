package com.pickgo.admin;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.pickgo.admin.service.AdminService;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.member.dto.MemberSimpleResponse;
import com.pickgo.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

	@Mock
	private MemberService memberService;

	@InjectMocks
	private AdminService adminService;

	@Test
	@DisplayName("getPagedMembers 성공")
	void getPagedMembers_success() {
		// given
		Pageable pageable = mock(Pageable.class);
		PageResponse<MemberSimpleResponse> expectedResponse = mock(PageResponse.class);

		when(memberService.getPagedMembers(pageable)).thenReturn(expectedResponse);

		// when
		PageResponse<MemberSimpleResponse> result = adminService.getPagedMembers(pageable);

		// then
		assertThat(result).isEqualTo(expectedResponse);
		verify(memberService, times(1)).getPagedMembers(pageable);
	}
}

