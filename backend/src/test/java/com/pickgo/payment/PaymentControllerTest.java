package com.pickgo.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.entity.enums.Authority;
import com.pickgo.domain.member.entity.enums.SocialProvider;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.payment.dto.PaymentConfirmRequest;
import com.pickgo.domain.payment.dto.PaymentCreateRequest;
import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;
import com.pickgo.domain.payment.repository.PaymentRepository;
import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.entity.PerformanceSession;
import com.pickgo.domain.performance.entity.PerformanceState;
import com.pickgo.domain.performance.entity.PerformanceType;
import com.pickgo.domain.performance.repository.PerformanceRepository;
import com.pickgo.domain.performance.repository.PerformanceSessionRepository;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.domain.reservation.repository.ReservationRepository;
import com.pickgo.domain.venue.entity.Venue;
import com.pickgo.domain.venue.repository.VenueRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.pickgo.global.response.RsCode.CREATED;
import static com.pickgo.global.response.RsCode.SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PaymentControllerTest {

    private final String testEmail = "test@example.com";
    private final String testPassword = "test_password";
    private final String testNickname = "test_user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private PerformanceSessionRepository performanceSessionRepository;

    private PerformanceSession performanceSession;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private UUID testMemberId;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        testMemberId = UUID.randomUUID();

        Member member = Member.builder()
                .id(testMemberId)
                .email(testEmail)
                .password(passwordEncoder.encode(testPassword))
                .nickname(testNickname)
                .authority(Authority.USER)
                .socialProvider(SocialProvider.NONE)
                .build();
        memberRepository.save(member);

        // 로그인 요청 (실제 로그인 API)
        String loginPayload = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(testEmail, testPassword);

        MvcResult result = mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn();

        // accessToken 추출
        String responseJson = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseJson);
        userToken = jsonNode.get("data").get("accessToken").asText();

        Venue venue = venueRepository.save(Venue.builder()
                .name("테스트 공연장")
                .address("서울시 테스트구")
                .build()
        );

        Performance performance = performanceRepository.save(Performance.builder()
                .name("테스트 공연")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .runtime("120분")
                .poster("test.jpg")
                .state(PerformanceState.SCHEDULED)
                .minAge("전체관람가")
                .casts("홍길동 외")
                .productionCompany("테스트컴퍼니")
                .type(PerformanceType.MUSICAL)
                .venue(venue)
                .build()
        );

        performanceSession = performanceSessionRepository.save(PerformanceSession.builder()
                .performance(performance)
                .performanceTime(LocalDateTime.now().plusDays(1))
                .build()
        );

    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
        reservationRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("결제 생성 성공")
    void createPayment() throws Exception {
        // Given: 예약이 먼저 필요함
        Member member = memberRepository.findByEmail(testEmail).orElseThrow();

        Reservation reservation = reservationRepository.save(Reservation.builder()
                .member(member)
                .performanceSession(performanceSession)
                .totalPrice(10000)
                .status(ReservationStatus.RESERVED)
                .build()
        );

        PaymentCreateRequest request = new PaymentCreateRequest(
                reservation.getTotalPrice(),
                reservation.getId()
        );

        mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(CREATED.getCode()))
                .andExpect(jsonPath("$.data.amount").value(reservation.getTotalPrice()))
                .andExpect(jsonPath("$.data.reservationId").value(reservation.getId()));
    }

    @Test
    @DisplayName("내 결제 목록 조회 성공")
    void getMyPayments() throws Exception {

        Member member = memberRepository.findByEmail(testEmail).orElseThrow();

        Reservation reservation1 = reservationRepository.save(Reservation.builder()
                .member(member)
                .performanceSession(performanceSession)
                .totalPrice(20000)
                .status(ReservationStatus.RESERVED)
                .build());

        Payment payment1 = paymentRepository.save(Payment.builder()
                .reservation(reservation1)
                .amount(20000)
                .status(PaymentStatus.PENDING)
                .build());

        Reservation reservation2 = reservationRepository.save(Reservation.builder()
                .member(member)
                .performanceSession(performanceSession)
                .totalPrice(30000)
                .status(ReservationStatus.RESERVED)
                .build());

        Payment payment2 = paymentRepository.save(Payment.builder()
                .reservation(reservation2)
                .amount(30000)
                .status(PaymentStatus.PENDING)
                .build());


        mockMvc.perform(get("/api/payments/me")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    @DisplayName("결제 상세 조회 성공")
    void getPaymentDetail() throws Exception {
        // Given: 예약 및 결제 저장
        Member member = memberRepository.findById(testMemberId).orElseThrow();

        Reservation reservation = reservationRepository.save(Reservation.builder()
                .member(member)
                .performanceSession(performanceSession)
                .totalPrice(15000)
                .status(ReservationStatus.RESERVED)
                .build());

        Payment payment = paymentRepository.save(Payment.builder()
                .reservation(reservation)
                .amount(15000)
                .status(PaymentStatus.PENDING)
                .build());

        mockMvc.perform(get("/api/payments/" + payment.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.reservationId").value(reservation.getId()))
                .andExpect(jsonPath("$.data.amount").value(15000));
    }

    @Test
    @DisplayName("결제 승인 성공")
    void confirmPayment() throws Exception {
        Member member = memberRepository.findById(testMemberId).orElseThrow();

        Reservation reservation = reservationRepository.save(Reservation.builder()
                .member(member)
                .performanceSession(performanceSession)
                .totalPrice(20000)
                .status(ReservationStatus.RESERVED)
                .build());

        Payment payment = paymentRepository.save(Payment.builder()
                .reservation(reservation)
                .amount(20000)
                .status(PaymentStatus.PENDING)
                .build());

        PaymentConfirmRequest request = new PaymentConfirmRequest(
                "dummyPaymentKey",
                "dummyOrderId",
                20000
        );

        mockMvc.perform(post("/api/payments/" + payment.getId() + "/confirm")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()));
    }

    @Test
    @DisplayName("결제 취소 성공")
    void cancelPayment() throws Exception {
        Member member = memberRepository.findById(testMemberId).orElseThrow();

        Reservation reservation = reservationRepository.save(Reservation.builder()
                .member(member)
                .performanceSession(performanceSession)
                .totalPrice(13000)
                .status(ReservationStatus.RESERVED)
                .build());

        Payment payment = paymentRepository.save(Payment.builder()
                .reservation(reservation)
                .amount(13000)
                .status(PaymentStatus.COMPLETED) // 테스트를 위해 승인 상태로 생성
                .build());

        mockMvc.perform(delete("/api/payments/" + payment.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SUCCESS.getCode()));
    }
}
