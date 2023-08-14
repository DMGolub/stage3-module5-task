package com.mjc.school.service.validator.impl;

import com.mjc.school.service.validator.ConstraintViolation;
import com.mjc.school.service.validator.Validator;
import com.mjc.school.service.validator.annotation.Constraint;
import com.mjc.school.service.validator.annotation.NotNull;
import com.mjc.school.service.validator.annotation.Valid;
import com.mjc.school.service.validator.checker.ConstraintChecker;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class ValidatorImpl implements Validator {

	private final Map<Class<? extends Annotation>, ConstraintChecker> checkersMap;

	public ValidatorImpl(final List<ConstraintChecker> checkers) {
		this.checkersMap =
			checkers.stream().collect(toMap(ConstraintChecker::getType, Function.identity()));
	}

	@Override
	public Set<ConstraintViolation> validate(final Object object, final Annotation[] parameterAnnotations) {
		Set<ConstraintViolation> violations = new HashSet<>();
		for (Annotation annotation : parameterAnnotations) {
			if (annotation instanceof Valid) {
				violations.addAll(validateObject(object));
			} else if (annotation instanceof NotNull) {
				if (object == null) {
					violations.add(
						new ConstraintViolation("Not null parameter constraint violated"));
				}
			} else {
				var checker = checkersMap.get(annotation.annotationType());
				var annotationType = annotation.annotationType();
				if (checker != null && !checker.check(object, annotationType.cast(annotation))) {
					violations.add(
						new ConstraintViolation(
							"Constraint '%s' violated for the value '%s'"
								.formatted(annotationType.getSimpleName(), object)));
				}
			}
		}

		return violations;
	}

	private Set<ConstraintViolation> validateObject(final Object object) {
		if (object == null) {
			return Collections.emptySet();
		}
		Set<ConstraintViolation> violations = new HashSet<>();
		for (var declaredField : object.getClass().getDeclaredFields()) {
			validateField(violations, declaredField, object);
		}
		return violations;
	}

	private void validateField(final Set<ConstraintViolation> violations, final Field field, final Object instance) {
		for (var declaredAnnotation : field.getDeclaredAnnotations()) {
			var annotationType = declaredAnnotation.annotationType();
			if (annotationType.isAnnotationPresent(Constraint.class)) {
				try {
					if (field.trySetAccessible() && field.canAccess(instance)) {
						var value = field.get(instance);
						var checker = checkersMap.get(annotationType);
						if (checker != null && !checker.check(value, annotationType.cast(declaredAnnotation))) {
							violations.add(
								new ConstraintViolation(
									"Constraint '%s' violated for the value '%s'"
										.formatted(annotationType.getSimpleName(), value)));
						}
						validateObject(violations, value);
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void validateObject(Set<ConstraintViolation> violations, final Object object) {
		if (object == null) {
			return;
		}
		for (var declaredField : object.getClass().getDeclaredFields()) {
			validateField(violations, declaredField, object);
		}
	}
}