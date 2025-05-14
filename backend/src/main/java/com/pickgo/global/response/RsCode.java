package com.pickgo.global.response;

import com.pickgo.global.exception.BusinessException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RsCode {

    // Common
    FORBIDDEN(RsConstant.FORBIDDEN, "접근 권한이 없습니다."),
    SUCCESS(RsConstant.SUCCESS, "요청이 성공했습니다."),
    CREATED(RsConstant.CREATED, "새로운 리소스를 생성했습니다."),
    NOT_FOUND(RsConstant.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),
    BAD_REQUEST(RsConstant.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER(RsConstant.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    UNAUTHENTICATED(RsConstant.UNAUTHORIZED, "인증이 실패했습니다."),
    UNAUTHORIZED(RsConstant.FORBIDDEN, "접근 권한이 없습니다."),
    //Performance
    PERFORMANCE_NOT_FOUND(RsConstant.NOT_FOUND, "공연 정보가 없습니다."),

    //Post
    POST_NOT_FOUND(RsConstant.NOT_FOUND, "게시글을 찾을 수 없습니다."),

    //Review
    REVIEW_CREATED(RsConstant.CREATED, "리뷰가 등록되었습니다."),
    REVIEW_DELETED(RsConstant.SUCCESS, "리뷰가 삭제되었습니다."),
    REVIEW_UPDATED(RsConstant.SUCCESS, "리뷰가 수정되었습니다."),
    REVIEW_NOT_FOUND(RsConstant.NOT_FOUND, "리뷰를 찾을수 없습니다."),
    REVIEW_ALREADY_LIKED(RsConstant.BAD_REQUEST, "이미 좋아요를 눌렀습니다."),
    REVIEW_NOT_LIKED_YET(RsConstant.NOT_FOUND, "좋아요를 누른 상태가 아닙니다."),

    // Member
    MEMBER_LOGIN_FAILED(RsConstant.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(RsConstant.NOT_FOUND, "존재하지 않는 유저입니다."),
    MEMBER_ALREADY_EXISTS(RsConstant.BAD_REQUEST, "이미 존재하는 유저입니다."),
    EMAIL_VERIFICATION_FAILED(RsConstant.BAD_REQUEST, "인증 실패 또는 코드 만료"),

    // Performance
    PERFORMANCE_SESSION_NOT_FOUND(RsConstant.NOT_FOUND, "존재하지 않는 공연 회차입니다."),

    // RESERVATION
    RESERVATION_NOT_FOUND(RsConstant.NOT_FOUND, "존재하지 않는 예약 내역입니다."),
    RESERVATION_CANCEL(RsConstant.SUCCESS, "예매가 취소되었습니다."),
    RESERVATION_EXPIRED(RsConstant.BAD_REQUEST, "예매 가능 시간이 초과되었습니다. 다시 예약해주세요"),
    INVALID_RESERVATION_STATE(RsConstant.BAD_REQUEST, "예약 상태가 부적합합니다."),
    RESERVATION_UNAVAILABLE(RsConstant.FORBIDDEN, "예매가 불가능한 회차입니다."),

    // S3
    FILE_UPLOAD_FAILED(RsConstant.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    FILE_DELETE_FAILED(RsConstant.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
    // SEAT
    SEAT_CONFLICT(RsConstant.CONFLICT, "이미 예약된 좌석이 포함되어 있습니다."),
    INVALID_SEAT_POSITION(RsConstant.BAD_REQUEST, "유효하지 않은 좌석입니다."),

    // Payment
    PAYMENT_INTEGRITY_ERROR(RsConstant.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),
    PAYMENT_TOSS_FAILED(RsConstant.INTERNAL_SERVER_ERROR, "토스 결제 승인 실패"),
    PAYMENT_TOSS_CANCEL_FAILED(RsConstant.INTERNAL_SERVER_ERROR, "토스 결제 취소 실패"),
    INVALID_PAYMENT_STATE(RsConstant.BAD_REQUEST, "결제 상태가 부적합 합니다."),
    PAYMENT_EXPIRED(RsConstant.BAD_REQUEST, "결제 가능 시간이 초과되었습니다.");

    private final Integer code;
    private final String message;

    public BusinessException toException() {
        return new BusinessException(this);
    }
}
