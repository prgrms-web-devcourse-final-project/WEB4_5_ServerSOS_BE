package com.pickgo.domain.member.admin.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.pickgo.domain.member.member.entity.enums.Authority;
import com.pickgo.domain.member.member.entity.enums.SocialProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.pickgo.domain.log.repository.MemberHistoryRepository;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.member.member.repository.MemberRepository;
import com.pickgo.global.logging.service.HistorySaveService;
import com.pickgo.global.response.RsCode;
import com.pickgo.global.token.TestToken;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TestToken token;

    @Autowired
    private HistorySaveService historySaveService;

    @Autowired
    private MemberHistoryRepository memberHistoryRepository;

    @BeforeEach
    void setUp() {
        memberRepository.saveAll(getTestMembers());
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    private List<Member> getTestMembers() {
        List<Member> members = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Member member = Member.builder()
                    .id(UUID.randomUUID())
                    .email("test" + i + "@example.com")
                    .password("password" + i)
                    .nickname("testUser" + i)
                    .authority(Authority.USER)
                    .socialProvider(SocialProvider.NONE)
                    .build();
            members.add(member);
        }
        return members;
    }

    @Test
    @DisplayName("ADMIN 권한이 있으면 유저 정보 조회 가능")
    void getMembers_권한있음_성공() throws Exception {
        // when & then
        mockMvc.perform(get("/api/admin/members")
                        .header("Authorization", "Bearer " + token.adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.totalElements").value(getTestMembers().size()));
    }

    @Test
    @DisplayName("ADMIN 권한이 없으면 유저 정보 조회 불가")
    void getMembers_권한없음_실패() throws Exception {
        // when & then
        mockMvc.perform(get("/api/admin/members")
                        .header("Authorization", "Bearer " + token.userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN - 회원 로그 조회 성공")
    void getMemberLogs_성공() throws Exception {
        mockMvc.perform(get("/api/admin/member-histories")
                        .header("Authorization", "Bearer " + token.adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.items").isArray());
    }


    @Test
    @DisplayName("ADMIN - 예약 로그 조회 성공")
    void getReservationLogs_성공() throws Exception {
        mockMvc.perform(get("/api/admin/reservation-histories")
                        .header("Authorization", "Bearer " + token.adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    @DisplayName("ADMIN - 결제 로그 조회 성공")
    void getPaymentLogs_성공() throws Exception {
        mockMvc.perform(get("/api/admin/payment-histories")
                        .header("Authorization", "Bearer " + token.adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(RsCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.items").isArray());
    }
}
