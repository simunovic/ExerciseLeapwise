package com.rss.feed.logging;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import com.rss.feed.logging.annotation.LogEntryExit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Aspect
public class LoggerAspect {

	/**
	 * Method is used for logging on entry and exit from methods annotated with
	 * LogEntryExit
	 * 
	 * @param point Joint point, method that is annotated with LogEntryExit
	 *              annotation
	 * @return Object that intercepted method returns
	 * @throws Throwable Throws propagated exception of intercepted method if one
	 *                   occurs
	 */
	@Around("@annotation(com.rss.feed.logging.annotation.LogEntryExit)")
	public Object log(ProceedingJoinPoint point) throws Throwable {

		CodeSignature codeSignature = (CodeSignature) point.getSignature();
		MethodSignature methodSignature = (MethodSignature) point.getSignature();

		Method method = methodSignature.getMethod();

		Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());
		LogEntryExit annotation = method.getAnnotation(LogEntryExit.class);

		LogLevel level = annotation.value();
		ChronoUnit unit = annotation.unit();

		boolean showArgs = annotation.showArgs();
		boolean showResult = annotation.showResult();
		boolean showExecutionTime = annotation.showExecutionTime();

		String methodName = method.getName();
		Object[] methodArgs = point.getArgs();
		String[] methodParams = codeSignature.getParameterNames();

		log(logger, level, entry(methodName, showArgs, methodParams, methodArgs));

		Instant start = Instant.now();
		Object response = point.proceed();
		Instant end = Instant.now();
		String duration = String.format("%s %s", unit.between(start, end), unit.name().toLowerCase());

		log(logger, level, exit(methodName, duration, response, showResult, showExecutionTime));

		return response;
	}

	/**
	 * Logging message with appropriated log level
	 * 
	 * @param logger  Logger instance that is responsible for logging
	 * @param level   Log level
	 * @param message Message that needs to be logged
	 */
	static void log(Logger logger, LogLevel level, String message) {
		switch (level) {
		case DEBUG:
			logger.debug(message);
		case TRACE:
			logger.trace(message);
		case WARN:
			logger.warn(message);
		case ERROR:
			logger.error(message);
		default:
			logger.info(message);
		}
	}

	/**
	 * Method is used for logging method invocation and arguments intercepted on
	 * entry
	 * 
	 * @param methodName Name of intercepted method
	 * @param showArgs   Flag to toggle whether to display the arguments received by
	 *                   a method
	 * @param params     Parameter names that intercepted method is invoked with
	 * @param args       Parameter values that intercepted method is invoked with
	 * @return Message that needs to be logged
	 */
	static String entry(String methodName, boolean showArgs, String[] params, Object[] args) {
		StringBuilder message = new StringBuilder().append("Started ").append(methodName).append(" method");

		if (showArgs && Objects.nonNull(params) && Objects.nonNull(args) && params.length == args.length) {
			Map<String, Object> values = new HashMap<>(params.length);
			for (int i = 0; i < params.length; i++) {
				values.put(params[i], args[i]);
			}
			message.append(" with args: ").append(values.toString());
		}

		return message.toString();
	}

	/**
	 * Method is used for logging method invocation and arguments intercepted on
	 * exit
	 * 
	 * @param methodName        Name of intercepted method
	 * @param duration          Execution duration of intercepted method
	 * @param result            Result that intercepted method returns
	 * @param showResult        Flag to toggle whether to display the result
	 *                          returned by the method
	 * @param showExecutionTime Flag to toggle whether you want to log the time it
	 *                          takes the method to finish executing
	 * @return Message that needs to be logged
	 */
	static String exit(String methodName, String duration, Object result, boolean showResult,
			boolean showExecutionTime) {
		StringBuilder message = new StringBuilder().append("Finished ").append(methodName).append(" method");

		if (showExecutionTime) {
			message.append(" in ").append(duration);
		}

		if (showResult) {
			message.append(" with return: ").append(result.toString());
		}

		return message.toString();
	}

}
