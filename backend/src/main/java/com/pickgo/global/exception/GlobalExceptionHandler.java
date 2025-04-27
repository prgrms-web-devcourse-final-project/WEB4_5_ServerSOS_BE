package com.pickgo.global.exception;

import static com.pickgo.global.response.RsCode.*;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pickgo.global.response.RsData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final String EXCEPTION_FORMAT = "[EXCEPTION]                   -----> ";
	private static final String EXCEPTION_MESSAGE_FORMAT = "[EXCEPTION] EXCEPTION_MESSAGE -----> [{}]";
	private static final String EXCEPTION_TYPE_FORMAT = "[EXCEPTION] EXCEPTION_TYPE    -----> [{}]";

	@ExceptionHandler(BusinessException.class)
	public RsData<?> handleBusinessException(
		final BusinessException exception
	) {
		return RsData.from(exception.getRsCode());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public RsData<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
		logWarn(exception);
		return RsData.from(BAD_REQUEST);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public RsData<?> handleIllegalArgumentException(
		final IllegalArgumentException exception
	) {
		logWarn(exception);
		return RsData.from(BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public RsData<?> handleMethodArgumentNotValidException(
		final MethodArgumentNotValidException exception
	) {
		logWarn(exception);
		return RsData.from(BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public RsData<?> handleInternalException(
		final Exception exception
	) {
		logError(exception);
		return RsData.from(INTERNAL_SERVER);
	}

	private void logError(Exception e) {
		log.error(EXCEPTION_TYPE_FORMAT, e.getClass().getSimpleName());
		log.error(EXCEPTION_MESSAGE_FORMAT, e.getMessage());
		log.error(EXCEPTION_FORMAT, e);
	}

	private void logWarn(Exception e) {
		log.warn(EXCEPTION_TYPE_FORMAT, e.getClass().getSimpleName());
		log.warn(EXCEPTION_MESSAGE_FORMAT, e.getMessage());
	}
}
