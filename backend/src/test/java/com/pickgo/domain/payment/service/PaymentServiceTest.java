package com.pickgo.domain.payment.service;

import static com.pickgo.domain.member.entity.enums.Authority.*;
import static com.pickgo.domain.member.entity.enums.SocialProvider.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.payment.dto.PaymentCreateRequest;
import com.pickgo.domain.payment.dto.PaymentDetailResponse;
import com.pickgo.domain.payment.dto.PaymentSimpleResponse;
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
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.RsCode;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private final String email = "test@example.com";
    private final String password = "test_password";
    private final String nickname = "test_user";

    private final UUID memberId = UUID.randomUUID();
    private final Long reservationId = 1L;
    private final Long paymentId = 10L;

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private PerformanceSessionRepository performanceSessionRepository;

    private PerformanceSession performanceSession;

    private Member getMockMember() {
        Member member = Member.builder()
                .id(memberId)
                .email(email)
                .password(password)
                .nickname(nickname)
                .authority(USER)
                .socialProvider(NONE)
                .build();

        // 수동으로 createdAt / modifiedAt 설정
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(member, "createdAt", now);
        ReflectionTestUtils.setField(member, "modifiedAt", now);
        return member;
    }

    private void setupPerformanceSession() {
        Venue venue = Venue.builder()
                .name("테스트 공연장")
                .address("서울시 테스트구")
                .build();

        Performance performance = Performance.builder()
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
                .build();

        performanceSession = PerformanceSession.builder()
                .performance(performance)
                .performanceTime(LocalDateTime.now().plusDays(1))
                .build();
    }

    private Reservation getMockReservation(Member member) {
        return Reservation.builder()
                .id(reservationId)
                .member(member)
                .performanceSession(performanceSession)
                .totalPrice(20000)
                .status(ReservationStatus.RESERVED)
                .build();
    }

    private Payment getMockPayment(Reservation reservation, PaymentStatus status) {
        return Payment.builder()
                .id(paymentId)
                .reservation(reservation)
                .amount(reservation.getTotalPrice())
                .status(status)
                .build();
    }

    private Reservation getMockReservation2(Member member) {
        return Reservation.builder()
                .id(2L)
                .member(member)
                .performanceSession(performanceSession)
                .totalPrice(30000)
                .status(ReservationStatus.RESERVED)
                .build();
    }

    private Payment getMockPayment2(Reservation reservation, PaymentStatus status) {
        return Payment.builder()
                .id(101L)
                .reservation(reservation)
                .amount(reservation.getTotalPrice())
                .status(status)
                .build();
    }

    @BeforeEach
    void setUp() {
        setupPerformanceSession();
    }

    @Test
    @DisplayName("결제 생성 성공")
    void createPayment_success() {
        Member member = getMockMember();
        Reservation reservation = getMockReservation(member);
        Payment payment = getMockPayment(reservation, PaymentStatus.PENDING);

        PaymentCreateRequest request = new PaymentCreateRequest(20000, reservationId);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentRepository.save(any())).thenReturn(payment);

        PaymentDetailResponse result = paymentService.createPayment(request);

        assertThat(result.amount()).isEqualTo(20000);
        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("내 결제 목록 조회 성공")
    void getMyPayments_success() {
        Member member = getMockMember();

        Reservation reservation1 = getMockReservation(member);
        Reservation reservation2 = getMockReservation2(member);

        Payment payment1 = getMockPayment(reservation1, PaymentStatus.CANCELED);
        Payment payment2 = getMockPayment2(reservation2, PaymentStatus.COMPLETED);

        PageRequest pageable = PageRequest.of(0, 10);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(paymentRepository.findByReservationMember(eq(member), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(payment1, payment2)));

        PageResponse<PaymentSimpleResponse> result = paymentService.getMyPayments(memberId, pageable);

        assertThat(result.items()).hasSize(2);
        assertThat(result.items().getFirst().amount()).isEqualTo(20000);
    }

    @Test
    @DisplayName("결제 상세 조회 성공")
    void getPaymentDetail_success() {
        Member member = getMockMember();
        Reservation reservation = getMockReservation(member);
        Payment payment = getMockPayment(reservation, PaymentStatus.COMPLETED);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        PaymentDetailResponse result = paymentService.getPaymentDetail(paymentId);

        assertThat(result.amount()).isEqualTo(20000);
        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    /*
     * 결제 승인 및 취소 테스트는 실제 결제 API와 연동되어야 하므로, MockMvc로 테스트하기 어려움.
     * 실제 결제 API와 연동하여 테스트하는 것이 좋음.
     * 아래 코드는 주석 처리함.
     */

//    @Test
//    @DisplayName("결제 승인 성공")
//    void confirmPayment_success() {
//        Member member = getMockMember();
//        Reservation reservation = getMockReservation(member);
//        Payment payment = getMockPayment(reservation, PaymentStatus.PENDING);
//
//        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
//
//        PaymentConfirmRequest request = new PaymentConfirmRequest("key", "order", 20000);
//
//        PaymentDetailResponse result = paymentService.confirmPayment(request);
//
//        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
//    }
//
//    @Test
//    @DisplayName("결제 취소 성공")
//    void cancelPayment_success() {
//        Member member = getMockMember();
//        Reservation reservation = getMockReservation(member);
//        Payment payment = getMockPayment(reservation, PaymentStatus.COMPLETED);
//
//        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
//
//        PaymentDetailResponse result = paymentService.cancelPayment(paymentId);
//
//        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.CANCELED);
//    }

    @Test
    @DisplayName("결제 취소 실패 - 상태가 COMPLETED 아님")
    void cancelPayment_fail_invalidStatus() {
        Member member = getMockMember();
        Reservation reservation = getMockReservation(member);
        Payment payment = getMockPayment(reservation, PaymentStatus.CANCELED);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.cancelPayment(paymentId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(RsCode.BAD_REQUEST.getMessage());
    }
}
