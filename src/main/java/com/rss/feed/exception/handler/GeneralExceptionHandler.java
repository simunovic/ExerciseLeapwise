package com.rss.feed.exception.handler;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GeneralExceptionHandler.class);

	private static final String FIELD_ERROR_SEPARATOR = ": ";
	private static final String LIST_JOIN_DELIMITER = ",";

	private static final String ERROR_MESSAGE_TEMPLATE = "message: %s %n requested uri: %s";

	private static final String PATH = "path";
	private static final String ERRORS = "error";
	private static final String STATUS = "status";
	private static final String MESSAGE = "message";
	private static final String TIMESTAMP = "timestamp";
	private static final String TYPE = "type";
	private static final String ERRORS_FOR_PATH = "errors {} for path {}";

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> validationErrors = exception.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + FIELD_ERROR_SEPARATOR + error.getDefaultMessage())
				.collect(Collectors.toList());
		return getExceptionResponseEntity(exception, HttpStatus.BAD_REQUEST, request, validationErrors);
	}

	/**
	 * Method is used for handling exceptions of type Exception which are
	 * thrown during runtime
	 * 
	 * @return ResponseEntity exception representation with corresponding details
	 */
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAllExceptions(Exception exception, WebRequest request) {
		ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);
		final HttpStatus status = responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
		final String localizedMessage = exception.getLocalizedMessage();
		final String path = request.getDescription(false);
		String message = (StringUtils.isNotEmpty(localizedMessage) ? localizedMessage : status.getReasonPhrase());
		logger.error(String.format(ERROR_MESSAGE_TEMPLATE, message, path), exception);
		return getExceptionResponseEntity(exception, status, request, Collections.singletonList(message));
	}

	private ResponseEntity<Object> getExceptionResponseEntity(final Exception exception, final HttpStatus status,
			final WebRequest request, final List<String> errors) {
		final Map<String, Object> body = new LinkedHashMap<>();
		final String path = request.getDescription(false);
		body.put(TIMESTAMP, Instant.now());
		body.put(STATUS, status.value());
		body.put(ERRORS, errors);
		body.put(TYPE, exception.getClass().getSimpleName());
		body.put(PATH, path);
		body.put(MESSAGE, status.getReasonPhrase());
		final String errorsMessage = CollectionUtils.isNotEmpty(errors)
				? errors.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(LIST_JOIN_DELIMITER))
				: status.getReasonPhrase();

		logger.error(ERRORS_FOR_PATH, errorsMessage, path);
		return new ResponseEntity<>(body, status);
	}

}
