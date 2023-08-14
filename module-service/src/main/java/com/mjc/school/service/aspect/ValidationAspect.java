package com.mjc.school.service.aspect;

import com.mjc.school.service.exception.ServiceErrorCode;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.validator.ConstraintViolation;
import com.mjc.school.service.validator.Validator;
import com.mjc.school.service.validator.annotation.Min;
import com.mjc.school.service.validator.annotation.NotNull;
import com.mjc.school.service.validator.annotation.Valid;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Aspect
@Component
public class ValidationAspect {

	private final Validator validator;

	public ValidationAspect(Validator validator) {
		this.validator = validator;
	}

	@Pointcut("execution(public * *(.., @com.mjc.school.service.validator.annotation.Valid (*), ..))")
	private void annotatedWithValid() {}

	@Pointcut("execution(public * *(.., @com.mjc.school.service.validator.annotation.NotNull (*), ..))")
	private void annotatedWithNotNull() {}

	@Pointcut("execution(public * *(.., @com.mjc.school.service.validator.annotation.Min (*), ..))")
	private void annotatedWithMin() {}

	@Before("annotatedWithValid() || annotatedWithNotNull() || annotatedWithMin()")
	public void validateBeforeInvocation(final JoinPoint joinPoint) throws NoSuchMethodException {
		if (joinPoint.getSignature() instanceof MethodSignature signature) {
			var targetMethod = getTargetMethod(joinPoint, signature);
			var args = joinPoint.getArgs();
			Annotation[][] parameterAnnotations = targetMethod.getParameterAnnotations();

			Set<ConstraintViolation> violations = new HashSet<>();
			for (int i = 0; i < parameterAnnotations.length; i++) {
				if (requiresValidation(parameterAnnotations[i])) {
					violations.addAll(validator.validate(args[i], parameterAnnotations[i]));
				}
			}

			if (!violations.isEmpty()) {
				throw new ValidationException(String.format(
					ServiceErrorCode.CONSTRAINT_VIOLATION.getMessage(), violations),
					ServiceErrorCode.CONSTRAINT_VIOLATION.getCode()
				);
			}
		}
	}

	private Method getTargetMethod(JoinPoint joinPoint, MethodSignature signature) throws NoSuchMethodException {
		Method baseMethod = signature.getMethod();
		return joinPoint.getTarget().getClass()
			.getMethod(baseMethod.getName(), baseMethod.getParameterTypes());
	}

	private boolean requiresValidation(final Annotation[] annotations) {
		return Stream.of(annotations)
			.anyMatch(a -> a instanceof Valid || a instanceof NotNull || a instanceof Min);
	}
}