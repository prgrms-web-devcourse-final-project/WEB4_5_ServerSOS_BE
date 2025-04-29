package com.pickgo.admin;

import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.global.response.RsCode;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.pickgo.domain.member.entity.enums.Authority.USER;
import static com.pickgo.domain.member.entity.enums.SocialProvider.NONE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TestToken token;

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
                    .authority(USER)
                    .socialProvider(NONE)
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
}
